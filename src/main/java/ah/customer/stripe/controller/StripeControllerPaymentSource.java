package ah.customer.stripe.controller;

import static ah.helper.StripeRequestHelper.ahResponseError;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentSourceCollection;
import com.stripe.model.Source;
import com.stripe.net.StripeResponse;
import com.stripe.param.PaymentSourceCollectionListParams;
import com.stripe.param.SourceCreateParams;
import com.stripe.param.SourceUpdateParams;

import ah.config.StripeConfig;
import ah.helper.StripeHelper;
import ah.rest.AhResponse;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/")
@Slf4j
public class StripeControllerPaymentSource {

    @Autowired
    public StripeControllerPaymentSource(StripeConfig config) {
        Stripe.apiKey = config.stripeSecretKey();
    }

    @GetMapping("/paymentSources/{customerCid}")
    public ResponseEntity<AhResponse<Source>> getSourcesForCustomer(
            @PathVariable("customerCid") String customerCid, @RequestBody String paymentSourceListParamString) {
        try {
            final Customer customer = Customer.retrieve(customerCid);
            final PaymentSourceCollectionListParams paymentSourceListParams =
                    StripeHelper.getGson().fromJson(paymentSourceListParamString, PaymentSourceCollectionListParams.class);

            final PaymentSourceCollection paymentSourceCollection = customer.getSources().list(paymentSourceListParams);
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

    @GetMapping("/paymentSource/{customerCid}/{paymentSourceCid}")
    public ResponseEntity<AhResponse<Source>> getSource(
            @PathVariable("customerCid") String customerCid, @PathVariable("paymentSourceCid") String paymentSourceCid) {
        try {
            final Source paymentSource = fetchSourceFromCustomer(customerCid, paymentSourceCid);
            return buildStripeResponseSource(paymentSource, "Error fetching Source");
        } catch (Exception e) {
            log.error("Error Fetching Source.", e);
            return AhResponse.internalError(e);
        }
    }

    @PostMapping("/paymentSource")
    public ResponseEntity<AhResponse<Source>> createSource(
            @PathVariable("customerCid") String customerCid, @RequestBody String paymentSourceCollectionCreateParamsString) {
        try {
            final SourceCreateParams sourceCreateParams =
                    StripeHelper.getGson().fromJson(paymentSourceCollectionCreateParamsString, SourceCreateParams.class);

            final Source paymentSourceNew = Source.create(sourceCreateParams);
            return buildStripeResponseSource(paymentSourceNew, "Error Creating Source");
        } catch (Exception e) {
            log.error("Error Creating Source.", e);
            return AhResponse.internalError(e);
        }
    }

    @PutMapping("/paymentSource/{paymentSourceCid}")
    public ResponseEntity<AhResponse<Source>> updateSource(
            @PathVariable("customerCid") String customerCid, @PathVariable("paymentSourceCid") String paymentSourceCid,
            @RequestBody String sourceUpdateParamsString) {
        try {
            final SourceUpdateParams sourceUpdateOnAccountParams =
                    StripeHelper.getGson().fromJson(sourceUpdateParamsString, SourceUpdateParams.class);

            final Source existingSource = fetchSourceFromCustomer(customerCid, paymentSourceCid);
            final Source updatedSource = existingSource.update(sourceUpdateOnAccountParams);
            return buildStripeResponseSource(updatedSource, "Error Updating Source");
        } catch (Exception e) {
            log.error("Error Updating Source.", e);
            return AhResponse.internalError(e);
        }
    }

    @DeleteMapping("/paymentSource/detach/{id}")
    public ResponseEntity<AhResponse<Source>> detachSourceFromCustomer(
            @PathVariable("customerCid") String customerCid, @PathVariable("paymentSourceCid") String paymentSourceCid) {
        try {
            final Source existingSource = fetchSourceFromCustomer(customerCid, paymentSourceCid);
            final Source deletedSource = existingSource.detach();
            return buildStripeResponseSource(deletedSource, "Error Deleting Source.");
        } catch (Exception e) {
            log.error("Error Removing Source.", e);
            return AhResponse.internalError(e);
        }
    }

    private Source fetchSourceFromCustomer(String customerCid, String paymentSourceCid) throws StripeException {
        final Customer customer = Customer.retrieve(customerCid);
        return (Source) customer.getSources().retrieve(paymentSourceCid);
    }

    private ResponseEntity<AhResponse<Source>> buildStripeResponseSource(Source paymentSource, String msg) {
        final StripeResponse lastResponse = paymentSource.getLastResponse();
        if (lastResponse.code() == HttpStatus.OK.value()) {
            final Source fetchedSource = StripeHelper.jsonToObject(lastResponse.body(), Source.class);
            return AhResponse.buildOk(fetchedSource);
        }
        return ahResponseError(msg, lastResponse.code(), paymentSource);
    }
}
