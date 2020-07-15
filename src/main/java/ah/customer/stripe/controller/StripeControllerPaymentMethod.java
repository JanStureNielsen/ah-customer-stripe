package ah.customer.stripe.controller;

import ah.config.StripeConfig;
import ah.helper.StripeHelper;
import ah.rest.AhResponse;
import com.stripe.Stripe;
import com.stripe.model.PaymentMethod;
import com.stripe.model.PaymentMethodCollection;
import com.stripe.net.StripeResponse;
import com.stripe.param.PaymentMethodAttachParams;
import com.stripe.param.PaymentMethodCreateParams;
import com.stripe.param.PaymentMethodListParams;
import com.stripe.param.PaymentMethodUpdateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static ah.helper.StripeRequestHelper.ahResponseError;

@RestController
@RequestMapping("/api/v1/")
@Slf4j
public class StripeControllerPaymentMethod {

    @Autowired
    public StripeControllerPaymentMethod(StripeConfig config) {
        Stripe.apiKey = config.stripeSecretKey();
    }

    @GetMapping("/paymentMethods")
    public ResponseEntity<AhResponse<PaymentMethod>> getPaymentMethodsForCustomer(
            @RequestBody String paymentMethodListParamString) {
        try {
            final PaymentMethodListParams paymentMethodListParams =
                    StripeHelper.getGson().fromJson(paymentMethodListParamString, PaymentMethodListParams.class);
            final PaymentMethodCollection paymentMethodCollection = PaymentMethod.list(paymentMethodListParams);
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

    @GetMapping("/paymentMethod/{id}")
    public ResponseEntity<AhResponse<PaymentMethod>> getPaymentMethod(@PathVariable("id") String paymentMethodCid) {
        try {
            final PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodCid);
            return buildStripeResponsePaymentMethod(paymentMethod, "Error fetching PaymentMethod");
        } catch (Exception e) {
            log.error("Error Fetching PaymentMethod.", e);
            return AhResponse.internalError(e);
        }
    }

    @PostMapping("/paymentMethod")
    public ResponseEntity<AhResponse<PaymentMethod>> createPaymentMethod(@RequestBody String paymentMethodCreateParamString) {
        try {
            final PaymentMethodCreateParams paymentMethodCreateParams =
                    StripeHelper.getGson().fromJson(paymentMethodCreateParamString, PaymentMethodCreateParams.class);
            final PaymentMethod paymentMethodNew = PaymentMethod.create(paymentMethodCreateParams);
            return buildStripeResponsePaymentMethod(paymentMethodNew, "Error Creating PaymentMethod");
        } catch (Exception e) {
            log.error("Error Creating PaymentMethod.", e);
            return AhResponse.internalError(e);
        }
    }

    @PutMapping("/paymentMethod/{id}")
    public ResponseEntity<AhResponse<PaymentMethod>> updatePaymentMethod(
            @PathVariable("id") String paymentMethodCid, @RequestBody String paymentMethodUpdateParamString) {
        try {
            final PaymentMethodUpdateParams paymentMethodUpdateParams = StripeHelper.getGson().fromJson(paymentMethodUpdateParamString, PaymentMethodUpdateParams.class);
            final PaymentMethod existingPaymentMethod = PaymentMethod.retrieve(paymentMethodCid);
            final PaymentMethod updatedPaymentMethod = existingPaymentMethod.update(paymentMethodUpdateParams);
            return buildStripeResponsePaymentMethod(updatedPaymentMethod, "Error Updating PaymentMethod");
        } catch (Exception e) {
            log.error("Error Updating PaymentMethod.", e);
            return AhResponse.internalError(e);
        }
    }

    // Not really delete, this detaches the payment method from a customer.
    @DeleteMapping("/paymentMethod/detach/{id}")
    public ResponseEntity<AhResponse<PaymentMethod>> detachPaymentMethodFromCustomer(@PathVariable("id") String paymentMethodCid) {
        try {
            final PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodCid);
            final PaymentMethod deletedPaymentMethod = paymentMethod.detach();
            return buildStripeResponsePaymentMethod(deletedPaymentMethod, "Error Detaching PaymentMethod.");
        } catch (Exception e) {
            log.error("Error Removing PaymentMethod.", e);
            return AhResponse.internalError(e);
        }
    }

    @PostMapping("/paymentMethod/attach/{id}")
    public ResponseEntity<AhResponse<PaymentMethod>> attachPaymentMethodToCustomer(
            @PathVariable("id") String paymentMethodCid, @RequestBody String paymentMethodAttachParamString) {
        try {
            final PaymentMethodAttachParams paymentMethodAttachParams =
                    StripeHelper.getGson().fromJson(paymentMethodAttachParamString, PaymentMethodAttachParams.class);

            final PaymentMethod existingPaymentMethod = PaymentMethod.retrieve(paymentMethodCid);
            final PaymentMethod attachedPymentMethod = existingPaymentMethod.attach(paymentMethodAttachParams);
            return buildStripeResponsePaymentMethod(attachedPymentMethod, "Error Attaching PaymentMethod.");
        } catch (Exception e) {
            log.error("Error Removing PaymentMethod.", e);
            final String errorMessage = e.getMessage();
            if (errorMessage.contains("cannot be deleted")) {
                return AhResponse.conflictError("PaymentMethod cannot be deleted, still has attached Price.", e);
            }
            return AhResponse.internalError(e);
        }
    }

    private ResponseEntity<AhResponse<PaymentMethod>> buildStripeResponsePaymentMethod(PaymentMethod paymentMethod, String msg) {
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
}
