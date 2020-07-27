package ah.customer.stripe.controller;

import ah.config.StripeConfig;
import ah.rest.AhResponse;
import com.stripe.Stripe;
import com.stripe.model.SubscriptionItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static ah.helper.HelperSubscriptionItem.*;

@RestController
@RequestMapping("/api/v1/subscriptionItems")
@Slf4j
public class StripeControllerSubscriptionItem {

    public static final String SUBSCRIPTION_AND_LARGE_LIMIT = "{ \"limit\": 9999999, \"subscription\": \"%s\" }";

    @Autowired
    public StripeControllerSubscriptionItem(StripeConfig config) {
        Stripe.apiKey = config.stripeSecretKey();
    }

    @GetMapping("/all/{scheduleCid}")
    public ResponseEntity<AhResponse<SubscriptionItem>> getSubscriptionItemsAll(@PathVariable("scheduleCid") String subscriptionCid) {
        return getSubscriptionItems(String.format(SUBSCRIPTION_AND_LARGE_LIMIT, subscriptionCid));
    }

    @GetMapping("/")
    public ResponseEntity<AhResponse<SubscriptionItem>> getSubscriptionItems(@RequestBody String subscriptionItemListParamsString) {
        return buildSubscriptionItemCollectionResponse(subscriptionItemsGet(subscriptionItemListParamsString));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AhResponse<SubscriptionItem>> getSubscriptionItem(@PathVariable("id") String subscriptionCid) {
        return buildSubscriptionItemResponse(subscriptionItemGet(subscriptionCid), "Error fetching Subscription Item");
    }

    @PostMapping("/")
    public ResponseEntity<AhResponse<SubscriptionItem>> createSubscriptionItem(@RequestBody String subscriptionCreateParamString) {
        return buildSubscriptionItemResponse(subscriptionItemCreate(subscriptionCreateParamString),
                "Error creating Subscription Item");
    }

    @PutMapping("/{id}")
    public ResponseEntity<AhResponse<SubscriptionItem>> updateSubscriptionItem(@PathVariable("id") String subscriptionCid, @RequestBody String subscriptionUpdateParamString) {
        return buildSubscriptionItemResponse(subscriptionItemUpdate(subscriptionCid, subscriptionUpdateParamString),
                "Error updating Subscription Item");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<AhResponse<SubscriptionItem>> deleteSubscriptionItem(@PathVariable("id") String subscriptionCid) {
        return buildSubscriptionItemResponse(subscriptionItemDelete(subscriptionCid),
                "Error deleting Subscription Item");
    }
}
