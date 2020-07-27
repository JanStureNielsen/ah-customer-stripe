package ah.helper;

import static ah.customer.stripe.StripeParam.PAYMENT_SOURCE_CREATE;
import static ah.customer.stripe.StripeParam.PAYMENT_SOURCE_LIST;
import static ah.customer.stripe.StripeParam.PAYMENT_SOURCE_UPDATE;
import static ah.helper.StripeHelper.runReturnOrThrow;
import static ah.helper.StripeRequestHelper.ahResponseError;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentSourceCollection;
import com.stripe.model.Source;
import com.stripe.net.StripeResponse;
import com.stripe.param.PaymentSourceCollectionListParams;
import com.stripe.param.SourceCreateParams;
import com.stripe.param.SourceUpdateParams;

import ah.rest.AhResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HelperPaymentSource {
    private HelperPaymentSource() {
    }

    public static PaymentSourceCollection paymentSourcesGet(String customerCid, String paymentSourceListParamString) {
        return runReturnOrThrow(() -> {
                    final Customer customer = Customer.retrieve(customerCid);
                    return customer.getSources().list((PaymentSourceCollectionListParams)
                            PAYMENT_SOURCE_LIST.fromJson(paymentSourceListParamString));
                },
                "PaymentMethods fetch failed for '%s'", paymentSourceListParamString);
    }

    public static Source paymentSourceGet(String customerCid, String paymentSourceCid) {
        return runReturnOrThrow(() -> fetchSourceFromCustomer(customerCid, paymentSourceCid),
                "PaymentSource fetch failed for '%s' '%s'", customerCid, paymentSourceCid);
    }

    public static Source paymentSourceCreate(String customerCid, String paymentSourceCollectionCreateParamsString) {
        return runReturnOrThrow(() -> Source.create((SourceCreateParams)
                        PAYMENT_SOURCE_CREATE.fromJson(paymentSourceCollectionCreateParamsString)),
                "PaymentSource creating failed for '%s' '%s'", customerCid, paymentSourceCollectionCreateParamsString);
    }

    public static Source paymentSourceUpdate(
            String customerCid, String paymentSourceCid, String sourceUpdateParamsString) {
        return runReturnOrThrow(() -> {
                    final Source existingSource = fetchSourceFromCustomer(customerCid, paymentSourceCid);
                    return existingSource.update((SourceUpdateParams)
                            PAYMENT_SOURCE_UPDATE.fromJson(sourceUpdateParamsString));
                },
                "PaymentSource updaing failed for '%s' '%s'", customerCid, sourceUpdateParamsString);
    }

    public static Source paymentSourceDetach(
            @PathVariable("customerCid") String customerCid, @PathVariable("paymentSourceCid") String paymentSourceCid) {
        return runReturnOrThrow(() -> {
                    final Source existingSource = fetchSourceFromCustomer(customerCid, paymentSourceCid);
                    return existingSource.detach();
                },
                "PaymentSource Detach failed for '%s' '%s'", customerCid, paymentSourceCid);
    }

    public static Source fetchSourceFromCustomer(String customerCid, String paymentSourceCid) throws StripeException {
        final Customer customer = Customer.retrieve(customerCid);
        return (Source) customer.getSources().retrieve(paymentSourceCid);
    }

    public static ResponseEntity<AhResponse<Source>> buildPaymentSourceResponse(Source paymentSource, String msg) {
        final StripeResponse lastResponse = paymentSource.getLastResponse();
        if (lastResponse.code() == HttpStatus.OK.value()) {
            final Source fetchedSource = StripeHelper.jsonToObject(lastResponse.body(), Source.class);
            return AhResponse.buildOk(fetchedSource);
        }
        return ahResponseError(msg, lastResponse.code(), paymentSource);
    }

    public static ResponseEntity<AhResponse<Source>> buildPaymentSourceCollectionResponse(
            PaymentSourceCollection paymentSourceCollection) {
        try {
            final StripeResponse lastResponse = paymentSourceCollection.getLastResponse();
            if (lastResponse.code() == HttpStatus.OK.value()) {
                final List<Source> sources =
                        paymentSourceCollection.getData().stream().map(ps -> (Source) ps).collect(Collectors.toList());
                return AhResponse.buildOk(sources);
            }
            final String errMsg = String.format("Error getting Sources : Code %d \n%s", lastResponse.code(),
                    StripeHelper.objectToJson(paymentSourceCollection));
            log.error(errMsg);
            return AhResponse.internalError(errMsg);
        } catch (Exception e) {
            log.error("Error Fetching Source.", e);
            return AhResponse.internalError(e);
        }
    }
}
