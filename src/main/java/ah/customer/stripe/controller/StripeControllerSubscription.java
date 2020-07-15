package ah.customer.stripe.controller;

import ah.config.StripeConfig;
import ah.helper.StripeHelper;
import ah.rest.AhResponse;
import com.stripe.Stripe;
import com.stripe.model.Subscription;
import com.stripe.model.SubscriptionCollection;
import com.stripe.net.StripeResponse;
import com.stripe.param.SubscriptionCreateParams;
import com.stripe.param.SubscriptionListParams;
import com.stripe.param.SubscriptionUpdateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static ah.helper.AhConstant.STRIPE_REST_LARGE_LIMIT;
import static ah.helper.StripeRequestHelper.ahResponseError;

@RestController
@RequestMapping("/api/v1/")
@Slf4j
public class StripeControllerSubscription {

    @Autowired
    public StripeControllerSubscription(StripeConfig config) {
        Stripe.apiKey = config.stripeSecretKey();
    }

    @GetMapping("/subscriptions/all")
    public ResponseEntity<AhResponse<Subscription>> getSubscriptions() {
        return getSubscriptions(STRIPE_REST_LARGE_LIMIT);
    }

    @GetMapping("/subscriptions")
    public ResponseEntity<AhResponse<Subscription>> getSubscriptions(@RequestBody String subscriptionListParamsString) {
        try {
            final SubscriptionListParams subscriptionListParams = StripeHelper.getGson().fromJson(subscriptionListParamsString, SubscriptionListParams.class);
            final SubscriptionCollection subscriptionCollection = Subscription.list(subscriptionListParams);

            final StripeResponse lastResponse = subscriptionCollection.getLastResponse();
            if (lastResponse.code() == HttpStatus.OK.value()) {
                return AhResponse.buildOk(subscriptionCollection.getData());
            }
            final String errMsg = String.format("Error getting subscriptions : Code %d \n%s", lastResponse.code(),
                    StripeHelper.objectToJson(subscriptionCollection));
            log.error(errMsg);
            return AhResponse.internalError(errMsg);

        } catch (Exception e) {
            log.error("Error Fetching Subscription.", e);
            return AhResponse.internalError(e);
        }
    }

    @GetMapping("/subscription/{id}")
    public ResponseEntity<AhResponse<Subscription>> getSubscription(@PathVariable("id") String subscriptionCid) {
        try {
            final Subscription subscription = Subscription.retrieve(subscriptionCid);
            return buildStripeResponseSubscription(subscription, "Error fetching Subscription");
        } catch (Exception e) {
            log.error("Error Fetching Subscription.", e);
            return AhResponse.internalError(e);
        }
    }

    @PostMapping("/subscription")
    public ResponseEntity<AhResponse<Subscription>> createSubscription(@RequestBody String subscriptionCreateParamString) {
        try {
            final SubscriptionCreateParams subscriptionCreateParams = StripeHelper.getGson().fromJson(subscriptionCreateParamString, SubscriptionCreateParams.class);
            final Subscription subscriptionNew = Subscription.create(subscriptionCreateParams);
            return buildStripeResponseSubscription(subscriptionNew, "Error Creating Subscription");
        } catch (Exception e) {
            log.error("Error Creating Subscription.", e);
            return AhResponse.internalError(e);
        }
    }

    @PutMapping("/subscription/{id}")
    public ResponseEntity<AhResponse<Subscription>> updateSubscription(@PathVariable("id") String subscriptionCid, @RequestBody String subscriptionUpdateParamString) {
        try {
            final SubscriptionUpdateParams subscriptionUpdateParams = StripeHelper.getGson().fromJson(subscriptionUpdateParamString, SubscriptionUpdateParams.class);
            final Subscription existingSubscription = Subscription.retrieve(subscriptionCid);
            final Subscription updatedSubscription = existingSubscription.update(subscriptionUpdateParams);
            return buildStripeResponseSubscription(updatedSubscription, "Error Updating Subscription");
        } catch (Exception e) {
            log.error("Error Updating Subscription.", e);
            return AhResponse.internalError(e);
        }
    }

    // No delete for Subscription, jsut Cancel.
    @DeleteMapping("/subscription/{id}")
    public ResponseEntity<AhResponse<Subscription>> cancelSubscription(@PathVariable("id") String subscriptionCid) {
        try {
            final Subscription subscription = Subscription.retrieve(subscriptionCid);
            final Subscription deletedSubscription = subscription.cancel();
            return buildStripeResponseSubscription(deletedSubscription, "Error Subscription.");
        } catch (Exception e) {
            log.error("Error Removing Subscription.", e);
            return AhResponse.internalError(e);
        }
    }

    private ResponseEntity<AhResponse<Subscription>> buildStripeResponseSubscription(Subscription subscription, String msg) {
        final StripeResponse lastResponse = subscription.getLastResponse();
        if (lastResponse.code() == HttpStatus.OK.value()) {
            final Subscription fetchedSubscription = StripeHelper.jsonToObject(lastResponse.body(), Subscription.class);
            return AhResponse.buildOk(fetchedSubscription);
        }
        return ahResponseError(msg, lastResponse.code(), subscription);
    }
}
