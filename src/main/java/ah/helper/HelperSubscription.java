package ah.helper;

import static ah.customer.stripe.StripeParam.SUBSCRIPTION_CREATE;
import static ah.customer.stripe.StripeParam.SUBSCRIPTION_LIST;
import static ah.customer.stripe.StripeParam.SUBSCRIPTION_UPDATE;
import static ah.helper.StripeHelper.runReturnOrThrow;
import static ah.rest.AhResponse.buildOk;
import static ah.rest.AhResponse.internalError;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.stripe.model.Subscription;
import com.stripe.model.SubscriptionCollection;
import com.stripe.net.StripeResponse;
import com.stripe.param.SubscriptionCreateParams;
import com.stripe.param.SubscriptionListParams;
import com.stripe.param.SubscriptionUpdateParams;

import ah.rest.AhResponse;

public class HelperSubscription {
    private HelperSubscription() {
    }

    public static SubscriptionCollection subscriptionsGet(String subscriptionListParamsString) {
        return runReturnOrThrow(() -> Subscription.list((SubscriptionListParams)
                        SUBSCRIPTION_LIST.fromJson(subscriptionListParamsString)),
                "Subscriptions fetch failed for '%s'", subscriptionListParamsString);
    }

    public static Subscription subscriptionGet(String subscriptionCid) {
        return runReturnOrThrow(() -> Subscription.retrieve(subscriptionCid),
                "Subscription fetch failed for '%s'", subscriptionCid);
    }

    public static Subscription subscriptionCreate(String subscriptionCreateParamString) {
        return runReturnOrThrow(() -> Subscription.create((SubscriptionCreateParams)
                        SUBSCRIPTION_CREATE.fromJson(subscriptionCreateParamString)),
                "Subscription create failed for '%s'", subscriptionCreateParamString);
    }

    public static Subscription subscriptionUpdate(String subscriptionCid, String subscriptionUpdateParamString) {
        return runReturnOrThrow(() -> {
                    final Subscription existingSubscription = Subscription.retrieve(subscriptionCid);
                    return existingSubscription.update((SubscriptionUpdateParams)
                            SUBSCRIPTION_UPDATE.fromJson(subscriptionUpdateParamString));
                },
                "Subscription create failed for '%s' '%s'", subscriptionCid, subscriptionUpdateParamString);
    }

    public static Subscription subscriptionCancel(String subscriptionCid) {
        return runReturnOrThrow(() -> {
                    final Subscription existingSubscription = Subscription.retrieve(subscriptionCid);
                    return existingSubscription.cancel();
                },
                "Subscription cancel failed for '%s' '%s'", subscriptionCid);
    }

    public static ResponseEntity<AhResponse<Subscription>> buildSubscriptionResponse(Subscription subscription, String msg) {
        final StripeResponse lastResponse = subscription.getLastResponse();
        if (lastResponse.code() == HttpStatus.OK.value()) {
            try {
                final Subscription fetchedSubscription = StripeHelper.jsonToObject(lastResponse.body(), Subscription.class);
                return buildOk(fetchedSubscription);
            } catch (Exception e) {
                subscription.setLastResponse(null);
                return buildOk(subscription);
            }
        }
        return internalError(msg, lastResponse.code(), subscription);
    }

    public static ResponseEntity<AhResponse<Subscription>> buildSubscriptionCollectionResponse(SubscriptionCollection subscriptionCollection) {
        String errorMessage = null;
        Exception errorCause = null;

        try {
            final StripeResponse lastResponse = subscriptionCollection.getLastResponse();
            if (lastResponse.code() == HttpStatus.OK.value()) {
                return buildOk(subscriptionCollection.getData());
            }
            errorMessage = String.format("Error getting subscriptions : Code %d \n%s", lastResponse.code(),
                    StripeHelper.objectToJson(subscriptionCollection));

        } catch (Exception e) {
            errorMessage = "Error Fetching Subscription.";
            errorCause = e;
        }

        return internalError(errorMessage, errorCause);
    }

}
