package ah.customer.stripe.controller;

import ah.config.StripeConfig;
import ah.rest.AhResponse;
import com.stripe.Stripe;
import com.stripe.model.Source;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static ah.helper.HelperPaymentSource.*;

@RestController
@RequestMapping("/api/v1/paymentSources")
@Slf4j
public class StripeControllerPaymentSource {

    @Autowired
    public StripeControllerPaymentSource(StripeConfig config) {
        Stripe.apiKey = config.stripeSecretKey();
    }

    @GetMapping("/{customerCid}")
    public ResponseEntity<AhResponse<Source>> getSourcesForCustomer(
            @PathVariable("customerCid") String customerCid, @RequestBody String paymentSourceListParamString) {
        return buildPaymentSourceCollectionResponse(paymentSourcesGet(customerCid, paymentSourceListParamString));
    }

    @GetMapping("/{customerCid}/{paymentSourceCid}")
    public ResponseEntity<AhResponse<Source>> getSource(
            @PathVariable("customerCid") String customerCid, @PathVariable("paymentSourceCid") String paymentSourceCid) {
        return buildPaymentSourceResponse(paymentSourceGet(customerCid, paymentSourceCid),
                "Error fetching Payment Source");
    }

    @PostMapping("/")
    public ResponseEntity<AhResponse<Source>> createSource(
            @PathVariable("customerCid") String customerCid, @RequestBody String paymentSourceCollectionCreateParamsString) {
        return buildPaymentSourceResponse(paymentSourceCreate(customerCid, paymentSourceCollectionCreateParamsString),
                "Error creating Payment Source");
    }

    @PutMapping("/{paymentSourceCid}")
    public ResponseEntity<AhResponse<Source>> updateSource(
            @PathVariable("customerCid") String customerCid, @PathVariable("paymentSourceCid") String paymentSourceCid,
            @RequestBody String sourceUpdateParamsString) {
        return buildPaymentSourceResponse(paymentSourceUpdate(customerCid, paymentSourceCid, sourceUpdateParamsString),
                "Error updating Payment Source");
    }

    @DeleteMapping("/detach/{id}")
    public ResponseEntity<AhResponse<Source>> detachSourceFromCustomer(
            @PathVariable("customerCid") String customerCid, @PathVariable("paymentSourceCid") String paymentSourceCid) {
        return buildPaymentSourceResponse(paymentSourceDetach(customerCid, paymentSourceCid),
                "Error detaching Payment Source");
    }
}
