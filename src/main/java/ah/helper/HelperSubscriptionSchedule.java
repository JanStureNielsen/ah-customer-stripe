package ah.helper;

import ah.rest.AhResponse;
import com.stripe.model.SubscriptionSchedule;
import com.stripe.model.SubscriptionScheduleCollection;
import com.stripe.net.StripeResponse;
import com.stripe.param.SubscriptionScheduleCreateParams;
import com.stripe.param.SubscriptionScheduleListParams;
import com.stripe.param.SubscriptionScheduleUpdateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static ah.customer.stripe.StripeParam.*;
import static ah.helper.StripeHelper.runReturnOrThrow;
import static ah.helper.StripeRequestHelper.ahResponseError;

@Slf4j
public class HelperSubscriptionSchedule {
    private HelperSubscriptionSchedule() {
    }

    public static SubscriptionScheduleCollection subscriptionSchedulesGet(String subscriptionListParamsString) {
        return runReturnOrThrow(() -> SubscriptionSchedule.list(
                (SubscriptionScheduleListParams) SUBSCRIPTION_SCHEDULE_LIST.fromJson(subscriptionListParamsString)),
                "Subscription Schedules fetch failed for '%s'", subscriptionListParamsString);
    }

    public static SubscriptionSchedule subscriptionScheduleGet(String subscriptionCid) {
        return runReturnOrThrow(() -> SubscriptionSchedule.retrieve(subscriptionCid),
                "Subscription Schedule fetch failed for '%s'", subscriptionCid);
    }

    public static SubscriptionSchedule subscriptionScheduleCreate(String subscriptionCreateParamString) {
        return runReturnOrThrow(() -> SubscriptionSchedule.create((SubscriptionScheduleCreateParams)
                        SUBSCRIPTION_SCHEDULE_CREATE.fromJson(subscriptionCreateParamString)),
                "Subscription Schedule create failed for '%s'", subscriptionCreateParamString);
    }

    public static SubscriptionSchedule subscriptionScheduleUpdate(String subscriptionCid, String subscriptionUpdateParamString) {
        return runReturnOrThrow(() -> {
            final SubscriptionSchedule existingSubscriptionSchedule = SubscriptionSchedule.retrieve(subscriptionCid);
            return existingSubscriptionSchedule.update((SubscriptionScheduleUpdateParams)
                    SUBSCRIPTION_SCHEDULE_UPDATE.fromJson(subscriptionUpdateParamString));
        }, "Subscription Schedule update failed for '%s'", subscriptionUpdateParamString);
    }

    public static SubscriptionSchedule subscriptionScheduleCancel(String subscriptionCid) {
        return runReturnOrThrow(() -> {
            final SubscriptionSchedule existingSubscriptionSchedule = SubscriptionSchedule.retrieve(subscriptionCid);
            return existingSubscriptionSchedule.cancel();
        }, "Subscription Schedule cancel failed for '%s'", subscriptionCid);
    }

    public static ResponseEntity<AhResponse<SubscriptionSchedule>> buildSubscriptionScheduleResponse(
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

    public static ResponseEntity<AhResponse<SubscriptionSchedule>> buildSubscriptionScheduleCollectionResponse(
            SubscriptionScheduleCollection subscriptionCollection) {
        try {
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

}