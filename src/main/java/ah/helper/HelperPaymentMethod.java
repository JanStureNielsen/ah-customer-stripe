package ah.helper;

import static ah.customer.stripe.StripeParam.PAYMENT_METHOD_ATTACH;
import static ah.customer.stripe.StripeParam.PAYMENT_METHOD_CREATE;
import static ah.customer.stripe.StripeParam.PAYMENT_METHOD_LIST;
import static ah.helper.StripeHelper.runReturnOrThrow;
import static ah.helper.StripeRequestHelper.ahResponseError;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.stripe.model.PaymentMethod;
import com.stripe.model.PaymentMethodCollection;
import com.stripe.net.StripeResponse;
import com.stripe.param.PaymentMethodAttachParams;
import com.stripe.param.PaymentMethodCreateParams;
import com.stripe.param.PaymentMethodListParams;
import com.stripe.param.PaymentMethodUpdateParams;

import ah.rest.AhResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HelperPaymentMethod {
    private HelperPaymentMethod() {
    }

    public static PaymentMethodCollection paymentMethodsFetch(String paymentMethodListParamString) {
        return runReturnOrThrow(() -> PaymentMethod.list((PaymentMethodListParams)
                        PAYMENT_METHOD_LIST.fromJson(paymentMethodListParamString)),
                "PaymentMethods fetch failed for '%s'", paymentMethodListParamString);
    }

    public static PaymentMethod paymentMethodFetch(String paymentMethodCid) {
        return runReturnOrThrow(() -> PaymentMethod.retrieve(paymentMethodCid),
                "PaymentMethod fetch failed for '%s'", paymentMethodCid);
    }

    public static PaymentMethod paymentMethodCreate(String paymentMethodCreateParamString) {
        return runReturnOrThrow(() -> PaymentMethod.create((PaymentMethodCreateParams)
                        PAYMENT_METHOD_CREATE.fromJson(paymentMethodCreateParamString)),
                "PaymentMethod create failed for '%s'", paymentMethodCreateParamString);
    }

    public static PaymentMethod paymentMethodUpdate(
            String paymentMethodCid, String paymentMethodUpdateParamString) {
        return runReturnOrThrow(() -> {
                    final PaymentMethod existingPaymentMethod = PaymentMethod.retrieve(paymentMethodCid);
                    return existingPaymentMethod.update((PaymentMethodUpdateParams)
                            PAYMENT_METHOD_CREATE.fromJson(paymentMethodUpdateParamString));
                },
                "PaymentMethod update failed for '%s' '%s'", paymentMethodCid, paymentMethodUpdateParamString);
    }

    public static PaymentMethod paymentMethodDetach(String paymentMethodCid) {
        return runReturnOrThrow(() -> {
                    final PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodCid);
                    return paymentMethod.detach();
                },
                "PaymentMethod detaching failed for '%s'", paymentMethodCid);
    }

    public static PaymentMethod paymentMethodAttach(
            String paymentMethodCid, String paymentMethodAttachParamString) {
        return runReturnOrThrow(() -> {
                    final PaymentMethod existingPaymentMethod = PaymentMethod.retrieve(paymentMethodCid);
                    return existingPaymentMethod.attach((PaymentMethodAttachParams) PAYMENT_METHOD_ATTACH.fromJson(paymentMethodAttachParamString));
                },
                "PaymentMethod attaching failed for '%s' '%s'", paymentMethodCid, paymentMethodAttachParamString);
    }

    public static ResponseEntity<AhResponse<PaymentMethod>> buildPaymentMethodResponse(PaymentMethod paymentMethod, String msg) {
        final StripeResponse lastResponse = paymentMethod.getLastResponse();
        if (lastResponse.code() == HttpStatus.OK.value()) {
            try {
                final PaymentMethod fetchedPaymentMethod = StripeHelper.jsonToObject(lastResponse.body(), PaymentMethod.class);
                return AhResponse.buildOk(fetchedPaymentMethod);
            } catch (Exception e) {
                paymentMethod.setLastResponse(null);
                return AhResponse.buildOk(paymentMethod);
            }
        }
        return ahResponseError(msg, lastResponse.code(), paymentMethod);
    }

    public static ResponseEntity<AhResponse<PaymentMethod>> buildCollectionResponse(PaymentMethodCollection paymentMethodCollection) {
        try {
            final StripeResponse lastResponse = paymentMethodCollection.getLastResponse();
            if (lastResponse.code() == HttpStatus.OK.value()) {
                return AhResponse.buildOk(paymentMethodCollection.getData());
            }
            final String errMsg = String.format("Error getting PaymentMethods : Code %d \n%s", lastResponse.code(),
                    StripeHelper.objectToJson(paymentMethodCollection));
            log.error(errMsg);
            return AhResponse.internalError(errMsg);
        } catch (Exception e) {
            log.error("Error Fetching PaymentMethod.", e);
            return AhResponse.internalError(e);
        }
    }
}
