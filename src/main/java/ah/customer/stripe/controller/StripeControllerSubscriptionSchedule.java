package ah.customer.stripe.controller;

import ah.config.StripeConfig;
import ah.helper.StripeHelper;
import ah.rest.AhResponse;
import com.stripe.Stripe;
import com.stripe.model.SubscriptionSchedule;
import com.stripe.model.SubscriptionScheduleCollection;
import com.stripe.net.StripeResponse;
import com.stripe.param.SubscriptionScheduleCreateParams;
import com.stripe.param.SubscriptionScheduleListParams;
import com.stripe.param.SubscriptionScheduleUpdateParams;
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
public class StripeControllerSubscriptionSchedule {

    public static final String SUBSCRIPTION_AND_LARGE_LIMIT = "{ \"limit\": 9999999, \"subscription\": \"%s\" }";

    @Autowired
    public StripeControllerSubscriptionSchedule(StripeConfig config) {
        Stripe.apiKey = config.stripeSecretKey();
    }

    @GetMapping("/subscriptionSchedules/all")
    public ResponseEntity<AhResponse<SubscriptionSchedule>> getSubscriptionSchedulesAll() {
        return getSubscriptionSchedules(STRIPE_REST_LARGE_LIMIT);
    }

    @GetMapping("/subscriptionSchedules")
    public ResponseEntity<AhResponse<SubscriptionSchedule>> getSubscriptionSchedules(@RequestBody String subscriptionListParamsString) {
        try {
            final SubscriptionScheduleListParams subscriptionListParams =
                    StripeHelper.getGson().fromJson(subscriptionListParamsString, SubscriptionScheduleListParams.class);

            final SubscriptionScheduleCollection subscriptionCollection =
                    SubscriptionSchedule.list(subscriptionListParams);

            final StripeResponse lastResponse = subscriptionCollection.getLastResponse();
            if (lastResponse.code() == HttpStatus.OK.value()) {
                return AhResponse.buildOk(subscriptionCollection.getData());
            }
            final String errMsg = String.format("Error getting subscriptions : Code %d \n%s", lastResponse.code(),
                    StripeHelper.objectToJson(subscriptionCollection));
            log.error(errMsg);
            return AhResponse.internalError(errMsg);

        } catch (Exception e) {
            log.error("Error Fetching SubscriptionSchedule.", e);
            return AhResponse.internalError(e);
        }
    }

    @GetMapping("/subscriptionSchedule/{id}")
    public ResponseEntity<AhResponse<SubscriptionSchedule>> getSubscriptionSchedule(@PathVariable("id") String subscriptionCid) {
        try {
            final SubscriptionSchedule subscription = SubscriptionSchedule.retrieve(subscriptionCid);
            return buildStripeResponseSubscriptionSchedule(subscription, "Error fetching SubscriptionSchedule");
        } catch (Exception e) {
            log.error("Error Fetching SubscriptionSchedule.", e);
            return AhResponse.internalError(e);
        }
    }

    @PostMapping("/subscriptionSchedule")
    public ResponseEntity<AhResponse<SubscriptionSchedule>> createSubscriptionSchedule(@RequestBody String subscriptionCreateParamString) {
        try {
            final SubscriptionScheduleCreateParams subscriptionCreateParams = StripeHelper.getGson().fromJson(subscriptionCreateParamString, SubscriptionScheduleCreateParams.class);
            final SubscriptionSchedule subscriptionNew = SubscriptionSchedule.create(subscriptionCreateParams);
            return buildStripeResponseSubscriptionSchedule(subscriptionNew, "Error Creating SubscriptionSchedule");
        } catch (Exception e) {
            log.error("Error Creating SubscriptionSchedule.", e);
            return AhResponse.internalError(e);
        }
    }

    @PutMapping("/subscriptionSchedule/{id}")
    public ResponseEntity<AhResponse<SubscriptionSchedule>> updateSubscriptionSchedule(@PathVariable("id") String subscriptionCid, @RequestBody String subscriptionUpdateParamString) {

        try {
            final SubscriptionScheduleUpdateParams subscriptionUpdateParams = StripeHelper.getGson().fromJson(subscriptionUpdateParamString, SubscriptionScheduleUpdateParams.class);
            final SubscriptionSchedule existingSubscriptionSchedule = SubscriptionSchedule.retrieve(subscriptionCid);
            final SubscriptionSchedule updatedSubscriptionSchedule = existingSubscriptionSchedule.update(subscriptionUpdateParams);
            return buildStripeResponseSubscriptionSchedule(updatedSubscriptionSchedule, "Error Updating SubscriptionSchedule");
        } catch (Exception e) {
            log.error("Error Updating SubscriptionSchedule.", e);
            return AhResponse.internalError(e);
        }
    }

    // Cancel, not delete
    @DeleteMapping("/subscriptionSchedule/{id}")
    public ResponseEntity<AhResponse<SubscriptionSchedule>> cancelSubscriptionSchedule(@PathVariable("id") String subscriptionCid) {
        try {
            final SubscriptionSchedule subscription = SubscriptionSchedule.retrieve(subscriptionCid);
            final SubscriptionSchedule deletedSubscriptionSchedule = subscription.cancel();
            return buildStripeResponseSubscriptionSchedule(deletedSubscriptionSchedule, "Error SubscriptionSchedule.");
        } catch (Exception e) {
            log.error("Error Removing SubscriptionSchedule.", e);
            return AhResponse.internalError(e);
        }
    }

    private ResponseEntity<AhResponse<SubscriptionSchedule>> buildStripeResponseSubscriptionSchedule(
            SubscriptionSchedule subscriptionSchedule, String msg) {
        final StripeResponse lastResponse = subscriptionSchedule.getLastResponse();
        if (lastResponse.code() == HttpStatus.OK.value()) {
            try {
                final SubscriptionSchedule fetchedSubscriptionSchedule = StripeHelper.jsonToObject(lastResponse.body(), SubscriptionSchedule.class);
                return AhResponse.buildOk(fetchedSubscriptionSchedule);
            } catch (Exception e) {
                subscriptionSchedule.setLastResponse(null);
                return AhResponse.buildOk(subscriptionSchedule);
            }
        }
        return ahResponseError(msg, lastResponse.code(), subscriptionSchedule);
    }
}
