package ah.customer.stripe.controller;

import static ah.helper.AhConstant.STRIPE_REST_LARGE_LIMIT;
import static ah.helper.HelperSubscriptionSchedule.buildSubscriptionScheduleCollectionResponse;
import static ah.helper.HelperSubscriptionSchedule.buildSubscriptionScheduleResponse;
import static ah.helper.HelperSubscriptionSchedule.subscriptionScheduleCancel;
import static ah.helper.HelperSubscriptionSchedule.subscriptionScheduleCreate;
import static ah.helper.HelperSubscriptionSchedule.subscriptionScheduleGet;
import static ah.helper.HelperSubscriptionSchedule.subscriptionScheduleUpdate;
import static ah.helper.HelperSubscriptionSchedule.subscriptionSchedulesGet;

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
import com.stripe.model.SubscriptionSchedule;

import ah.config.StripeConfig;
import ah.rest.AhResponse;

@RestController
@RequestMapping("/api/v1/subscriptionSchedules")
public class StripeControllerSubscriptionSchedule {

    public static final String SUBSCRIPTION_AND_LARGE_LIMIT = "{ \"limit\": 9999999, \"subscription\": \"%s\" }";

    @Autowired
    public StripeControllerSubscriptionSchedule(StripeConfig config) {
        Stripe.apiKey = config.stripeSecretKey();
    }

    @GetMapping("/all")
    public ResponseEntity<AhResponse<SubscriptionSchedule>> getSubscriptionSchedulesAll() {
        return getSubscriptionSchedules(STRIPE_REST_LARGE_LIMIT);
    }

    @GetMapping("/")
    public ResponseEntity<AhResponse<SubscriptionSchedule>> getSubscriptionSchedules(@RequestBody String subscriptionListParamsString) {
        return buildSubscriptionScheduleCollectionResponse(subscriptionSchedulesGet(subscriptionListParamsString));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AhResponse<SubscriptionSchedule>> getSubscriptionSchedule(@PathVariable("id") String subscriptionCid) {
        return buildSubscriptionScheduleResponse(subscriptionScheduleGet(subscriptionCid),
                "Error fetching Subscription Schedule");
    }

    @PostMapping("/")
    public ResponseEntity<AhResponse<SubscriptionSchedule>> createSubscriptionSchedule(@RequestBody String subscriptionCreateParamString) {
        return buildSubscriptionScheduleResponse(subscriptionScheduleCreate(subscriptionCreateParamString),
                "Error creating Subscription Schedule");
    }

    @PutMapping("/{id}")
    public ResponseEntity<AhResponse<SubscriptionSchedule>> updateSubscriptionSchedule(@PathVariable("id") String subscriptionCid, @RequestBody String subscriptionUpdateParamString) {
        return buildSubscriptionScheduleResponse(subscriptionScheduleUpdate(subscriptionCid, subscriptionUpdateParamString),
                "Error updating Subscription Schedule");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AhResponse<SubscriptionSchedule>> cancelSubscriptionSchedule(@PathVariable("id") String subscriptionCid) {
        return buildSubscriptionScheduleResponse(subscriptionScheduleCancel(subscriptionCid),
                "Error canceling Subscription Schedule");
    }
}
