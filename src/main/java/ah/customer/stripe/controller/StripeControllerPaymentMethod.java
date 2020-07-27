package ah.customer.stripe.controller;

import static ah.helper.HelperPaymentMethod.buildCollectionResponse;
import static ah.helper.HelperPaymentMethod.buildPaymentMethodResponse;
import static ah.helper.HelperPaymentMethod.paymentMethodAttach;
import static ah.helper.HelperPaymentMethod.paymentMethodCreate;
import static ah.helper.HelperPaymentMethod.paymentMethodDetach;
import static ah.helper.HelperPaymentMethod.paymentMethodFetch;
import static ah.helper.HelperPaymentMethod.paymentMethodUpdate;
import static ah.helper.HelperPaymentMethod.paymentMethodsFetch;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.stripe.model.PaymentMethod;

import ah.config.StripeConfig;
import ah.rest.AhResponse;

@RestController
@RequestMapping("/api/v1/paymentMethods")
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
