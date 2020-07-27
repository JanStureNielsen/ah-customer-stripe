package ah.customer.stripe.controller;

import static ah.helper.HelperSubscriptionItem.buildSubscriptionItemCollectionResponse;
import static ah.helper.HelperSubscriptionItem.buildSubscriptionItemResponse;
import static ah.helper.HelperSubscriptionItem.subscriptionItemCreate;
import static ah.helper.HelperSubscriptionItem.subscriptionItemDelete;
import static ah.helper.HelperSubscriptionItem.subscriptionItemGet;
import static ah.helper.HelperSubscriptionItem.subscriptionItemUpdate;
import static ah.helper.HelperSubscriptionItem.subscriptionItemsGet;

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
import com.stripe.model.SubscriptionItem;

import ah.config.StripeConfig;
import ah.rest.AhResponse;

@RestController
@RequestMapping("/api/v1/subscriptionItems")
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
