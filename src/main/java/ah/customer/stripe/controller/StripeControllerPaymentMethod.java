package ah.customer.stripe.controller;

import ah.config.StripeConfig;
import ah.helper.StripeHelper;
import ah.rest.AhResponse;
import com.stripe.Stripe;
import com.stripe.model.PaymentMethod;
import com.stripe.model.PaymentMethodCollection;
import com.stripe.net.StripeResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static ah.helper.HelperPaymentMethod.*;
import static ah.helper.StripeRequestHelper.ahResponseError;

@RestController
@RequestMapping("/api/v1/paymentMethods")
@Slf4j
public class StripeControllerPaymentMethod {

    @Autowired
    public StripeControllerPaymentMethod(StripeConfig config) {
        Stripe.apiKey = config.stripeSecretKey();
    }

    @GetMapping("/")
    public ResponseEntity<AhResponse<PaymentMethod>> getPaymentMethodsForCustomer(
            @RequestBody String paymentMethodListParamString) {
        return buildCollectionResponse(paymentMethodsFetch(paymentMethodListParamString));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AhResponse<PaymentMethod>> getPaymentMethod(@PathVariable("id") String paymentMethodCid) {
        return buildPaymentMethodResponse(paymentMethodFetch(paymentMethodCid), "Error fetching PaymentMethod");
    }

    @PostMapping("/")
    public ResponseEntity<AhResponse<PaymentMethod>> createPaymentMethod(@RequestBody String paymentMethodCreateParamString) {
        return buildPaymentMethodResponse(paymentMethodCreate(paymentMethodCreateParamString),
                "Error creating PaymentMethod");
    }

    @PutMapping("/{id}")
    public ResponseEntity<AhResponse<PaymentMethod>> updatePaymentMethod(
            @PathVariable("id") String paymentMethodCid, @RequestBody String paymentMethodUpdateParamString) {
        return buildPaymentMethodResponse(paymentMethodUpdate(paymentMethodCid, paymentMethodUpdateParamString),
                "Error updating PaymentMethod");
    }

    // Not really delete, this detaches the payment method from a customer.
    @DeleteMapping("/detach/{id}")
    public ResponseEntity<AhResponse<PaymentMethod>> detachPaymentMethodFromCustomer(@PathVariable("id") String paymentMethodCid) {
        return buildPaymentMethodResponse(paymentMethodDetach(paymentMethodCid),
                "Error detaching PaymentMethod");
    }

    @PostMapping("/attach/{id}")
    public ResponseEntity<AhResponse<PaymentMethod>> attachPaymentMethodToCustomer(
            @PathVariable("id") String paymentMethodCid, @RequestBody String paymentMethodAttachParamString) {
        return buildPaymentMethodResponse(paymentMethodAttach(paymentMethodCid, paymentMethodAttachParamString),
                "Error attaching PaymentMethod");
    }
}
