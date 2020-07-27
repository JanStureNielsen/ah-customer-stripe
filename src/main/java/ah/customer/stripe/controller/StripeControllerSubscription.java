package ah.customer.stripe.controller;

import ah.config.StripeConfig;
import ah.rest.AhResponse;
import com.stripe.Stripe;
import com.stripe.model.Subscription;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static ah.helper.AhConstant.STRIPE_REST_LARGE_LIMIT;
import static ah.helper.HelperSubscription.*;

@RestController
@RequestMapping("/api/v1/subscriptions")
@Slf4j
public class StripeControllerSubscription {

    @Autowired
    public StripeControllerSubscription(StripeConfig config) {
        Stripe.apiKey = config.stripeSecretKey();
    }

    @GetMapping("/all")
    public ResponseEntity<AhResponse<Subscription>> getSubscriptions() {
        return getSubscriptions(STRIPE_REST_LARGE_LIMIT);
    }

    @GetMapping("/")
    public ResponseEntity<AhResponse<Subscription>> getSubscriptions(@RequestBody String subscriptionListParamsString) {
        return buildSubscriptionCollectionResponse(subscriptionsGet(subscriptionListParamsString));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AhResponse<Subscription>> getSubscription(@PathVariable("id") String subscriptionCid) {
        return buildSubscriptionResponse(subscriptionGet(subscriptionCid), "Error fetching subscription");
    }

    @PostMapping("/")
    public ResponseEntity<AhResponse<Subscription>> createSubscription(@RequestBody String subscriptionCreateParamString) {
        return buildSubscriptionResponse(subscriptionCreate(subscriptionCreateParamString),
                "Error creating subscription");
    }

    @PutMapping("/{id}")
    public ResponseEntity<AhResponse<Subscription>> updateSubscription(@PathVariable("id") String subscriptionCid, @RequestBody String subscriptionUpdateParamString) {
        return buildSubscriptionResponse(subscriptionUpdate(subscriptionCid, subscriptionUpdateParamString),
                "Error updating subscription");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AhResponse<Subscription>> cancelSubscription(@PathVariable("id") String subscriptionCid) {
        return buildSubscriptionResponse(subscriptionCancel(subscriptionCid),
                "Error canceling subscription");
    }
}
