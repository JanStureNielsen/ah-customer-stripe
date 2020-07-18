package ah.customer.stripe.controller;

import ah.config.StripeConfig;
import ah.helper.StripeHelper;
import ah.rest.AhResponse;
import com.stripe.Stripe;
import com.stripe.model.SubscriptionItem;
import com.stripe.model.SubscriptionItemCollection;
import com.stripe.net.StripeResponse;
import com.stripe.param.SubscriptionItemCreateParams;
import com.stripe.param.SubscriptionItemListParams;
import com.stripe.param.SubscriptionItemUpdateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static ah.helper.StripeRequestHelper.ahResponseError;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class StripeControllerSubscriptionItem {

    public static final String SUBSCRIPTION_AND_LARGE_LIMIT = "{ \"limit\": 9999999, \"subscription\": \"%s\" }";

    @Autowired
    public StripeControllerSubscriptionItem(StripeConfig config) {
        Stripe.apiKey = config.stripeSecretKey();
    }

    @GetMapping("/subscriptionItems/all/{scheduleCid}")
    public ResponseEntity<AhResponse<SubscriptionItem>> getSubscriptionItemsAll(@PathVariable("scheduleCid") String subscriptionCid) {
        return getSubscriptionItems(String.format(SUBSCRIPTION_AND_LARGE_LIMIT, subscriptionCid));
    }

    @GetMapping("/subscriptionItems")
    public ResponseEntity<AhResponse<SubscriptionItem>> getSubscriptionItems(@RequestBody String subscriptionItemListParamsString) {
        try {
            final SubscriptionItemListParams subscriptionItemListParams = StripeHelper.getGson().fromJson(subscriptionItemListParamsString, SubscriptionItemListParams.class);
            final SubscriptionItemCollection subscriptionItemCollection = SubscriptionItem.list(subscriptionItemListParams);

            final StripeResponse lastResponse = subscriptionItemCollection.getLastResponse();
            if (lastResponse.code() == HttpStatus.OK.value()) {
                return AhResponse.buildOk(subscriptionItemCollection.getData());
            }
            final String errMsg = String.format("Error getting subscriptions : Code %d \n%s", lastResponse.code(),
                    StripeHelper.objectToJson(subscriptionItemCollection));
            log.error(errMsg);
            return AhResponse.internalError(errMsg);
        } catch (Exception e) {
            log.error("Error Fetching SubscriptionItem.", e);
            return AhResponse.internalError(e);
        }
    }

    @GetMapping("/subscriptionItem/{id}")
    public ResponseEntity<AhResponse<SubscriptionItem>> getSubscriptionItem(@PathVariable("id") String subscriptionCid) {
        try {
            final SubscriptionItem subscription = SubscriptionItem.retrieve(subscriptionCid);
            return buildStripeResponseSubscriptionItem(subscription, "Error fetching SubscriptionItem");
        } catch (Exception e) {
            log.error("Error Fetching SubscriptionItem.", e);
            return AhResponse.internalError(e);
        }
    }

    @PostMapping("/subscriptionItem")
    public ResponseEntity<AhResponse<SubscriptionItem>> createSubscriptionItem(@RequestBody String subscriptionCreateParamString) {
        try {
            final SubscriptionItemCreateParams subscriptionCreateParams = StripeHelper.getGson().fromJson(subscriptionCreateParamString, SubscriptionItemCreateParams.class);
            final SubscriptionItem subscriptionNew = SubscriptionItem.create(subscriptionCreateParams);
            return buildStripeResponseSubscriptionItem(subscriptionNew, "Error Creating SubscriptionItem");
        } catch (Exception e) {
            log.error("Error Creating SubscriptionItem.", e);
            return AhResponse.internalError(e);
        }
    }

    @PutMapping("/subscriptionItem/{id}")
    public ResponseEntity<AhResponse<SubscriptionItem>> updateSubscriptionItem(@PathVariable("id") String subscriptionCid, @RequestBody String subscriptionUpdateParamString) {

        try {
            final SubscriptionItemUpdateParams subscriptionUpdateParams = StripeHelper.getGson().fromJson(subscriptionUpdateParamString, SubscriptionItemUpdateParams.class);
            final SubscriptionItem existingSubscriptionItem = SubscriptionItem.retrieve(subscriptionCid);
            final SubscriptionItem updatedSubscriptionItem = existingSubscriptionItem.update(subscriptionUpdateParams);
            return buildStripeResponseSubscriptionItem(updatedSubscriptionItem, "Error Updating SubscriptionItem");
        } catch (Exception e) {
            log.error("Error Updating SubscriptionItem.", e);
            return AhResponse.internalError(e);
        }
    }

    @DeleteMapping("/subscriptionItem/delete/{id}")
    public ResponseEntity<AhResponse<SubscriptionItem>> deleteSubscriptionItem(@PathVariable("id") String subscriptionCid) {
        try {
            final SubscriptionItem subscription = SubscriptionItem.retrieve(subscriptionCid);
            final SubscriptionItem deletedSubscriptionItem = subscription.delete();
            return buildStripeResponseSubscriptionItem(deletedSubscriptionItem, "Error SubscriptionItem.");
        } catch (Exception e) {
            log.error("Error Removing SubscriptionItem.", e);
            return AhResponse.internalError(e);
        }
    }

    private ResponseEntity<AhResponse<SubscriptionItem>> buildStripeResponseSubscriptionItem(SubscriptionItem subscriptionItem, String msg) {
        final StripeResponse lastResponse = subscriptionItem.getLastResponse();
        if (lastResponse.code() == HttpStatus.OK.value()) {
            final SubscriptionItem fetchedSubscriptionItem = StripeHelper.jsonToObject(lastResponse.body(), SubscriptionItem.class);
            return AhResponse.buildOk(fetchedSubscriptionItem);
        }
        return ahResponseError(msg, lastResponse.code(), subscriptionItem);
    }
}
