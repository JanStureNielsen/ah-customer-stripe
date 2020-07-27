package ah.helper;

import static ah.customer.stripe.StripeParam.SUBSCRIPTION_ITEM_CREATE;
import static ah.customer.stripe.StripeParam.SUBSCRIPTION_ITEM_LIST;
import static ah.customer.stripe.StripeParam.SUBSCRIPTION_ITEM_UPDATE;
import static ah.helper.StripeHelper.runReturnOrThrow;
import static ah.rest.AhResponse.buildOk;
import static ah.rest.AhResponse.internalError;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.stripe.model.SubscriptionItem;
import com.stripe.model.SubscriptionItemCollection;
import com.stripe.net.StripeResponse;
import com.stripe.param.SubscriptionItemCreateParams;
import com.stripe.param.SubscriptionItemListParams;
import com.stripe.param.SubscriptionItemUpdateParams;

import ah.rest.AhResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HelperSubscriptionItem {
    private HelperSubscriptionItem() {
    }

    public static SubscriptionItemCollection subscriptionItemsGet(String subscriptionItemListParamsString) {
        return runReturnOrThrow(() -> SubscriptionItem.list((SubscriptionItemListParams)
                        SUBSCRIPTION_ITEM_LIST.fromJson(subscriptionItemListParamsString)),
                "Subscriptions fetch failed for '%s'", subscriptionItemListParamsString);
    }

    public static SubscriptionItem subscriptionItemGet(String subscriptionCid) {
        return runReturnOrThrow(() -> SubscriptionItem.retrieve(subscriptionCid),
                "Subscription fetch failed for '%s'", subscriptionCid);
    }

    public static SubscriptionItem subscriptionItemCreate(@RequestBody String subscriptionCreateParamString) {
        return runReturnOrThrow(() -> SubscriptionItem.create((SubscriptionItemCreateParams)
                        SUBSCRIPTION_ITEM_CREATE.fromJson(subscriptionCreateParamString)),
                "Subscription create failed for '%s'", subscriptionCreateParamString);
    }

    public static SubscriptionItem subscriptionItemUpdate(@PathVariable("id") String subscriptionCid, @RequestBody String subscriptionUpdateParamString) {
        return runReturnOrThrow(() -> {
            final SubscriptionItem existingSubscriptionItem = SubscriptionItem.retrieve(subscriptionCid);
            return existingSubscriptionItem.update((SubscriptionItemUpdateParams)
                    SUBSCRIPTION_ITEM_UPDATE.fromJson(subscriptionUpdateParamString));
        }, "Subscription update failed for '%s'", subscriptionUpdateParamString);
    }

    public static SubscriptionItem subscriptionItemDelete(@PathVariable("id") String subscriptionCid) {
        return runReturnOrThrow(() -> {
            final SubscriptionItem existingSubscriptionItem = SubscriptionItem.retrieve(subscriptionCid);
            return existingSubscriptionItem.delete();
        }, "Subscription delete failed for '%s'", subscriptionCid);
    }

    public static ResponseEntity<AhResponse<SubscriptionItem>> buildSubscriptionItemCollectionResponse(
            SubscriptionItemCollection subscriptionItemCollection) {
        try {
            final StripeResponse lastResponse = subscriptionItemCollection.getLastResponse();
            if (lastResponse.code() == HttpStatus.OK.value()) {
                return buildOk(subscriptionItemCollection.getData());
            }
            final String errMsg = String.format("Error getting subscriptions : Code %d \n%s", lastResponse.code(),
                    StripeHelper.objectToJson(subscriptionItemCollection));
            log.error(errMsg);
            return internalError(errMsg);
        } catch (Exception e) {
            log.error("Error Fetching SubscriptionItem.", e);
            return internalError(e);
        }
    }

    public static ResponseEntity<AhResponse<SubscriptionItem>> buildSubscriptionItemResponse(SubscriptionItem subscriptionItem, String msg) {
        final StripeResponse lastResponse = subscriptionItem.getLastResponse();
        if (lastResponse.code() == HttpStatus.OK.value()) {
            final SubscriptionItem fetchedSubscriptionItem = StripeHelper.jsonToObject(lastResponse.body(), SubscriptionItem.class);
            return buildOk(fetchedSubscriptionItem);
        }
        return internalError(msg, lastResponse.code(), subscriptionItem);
    }

}