package ah.helper;

import ah.rest.AhResponse;
import com.stripe.model.Price;
import com.stripe.model.PriceCollection;
import com.stripe.net.StripeResponse;
import com.stripe.param.PriceCreateParams;
import com.stripe.param.PriceListParams;
import com.stripe.param.PriceUpdateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static ah.customer.stripe.StripeParam.*;
import static ah.helper.StripeHelper.inactive;
import static ah.helper.StripeHelper.runReturnOrThrow;
import static ah.helper.StripeRequestHelper.ahResponseError;

@Slf4j
public class HelperPrice {
    private HelperPrice() {
    }

    public static PriceCollection pricesGet(String priceListParamsString) {
        return runReturnOrThrow(() -> Price.list((PriceListParams) PRICE_LIST.fromJson(priceListParamsString)),
                "Prices fetch failed for '%s'", priceListParamsString);
    }

    public static Price priceGet(String priceCid) {
        return runReturnOrThrow(() -> Price.retrieve(priceCid),
                "Price fetch failed for '%s'", priceCid);
    }

    public static Price priceCreate(String priceCreateParamsString) {
        return runReturnOrThrow(() -> Price.create((PriceCreateParams)
                        PRICE_CREATE.fromJson(priceCreateParamsString)),
                "Price create failed for '%s'", priceCreateParamsString);
    }

    public static Price priceUpdate(String priceCid, String priceUpdateParamsString) {
        return runReturnOrThrow(() -> {
                    final Price existingPrice = Price.retrieve(priceCid);
                    return existingPrice.update((PriceUpdateParams) PRICE_UPDATE.fromJson(priceUpdateParamsString));
                },
                "Price update failed for '%s'", priceUpdateParamsString);
    }

    public static Price priceInactive(String priceCid) {
        return runReturnOrThrow(() -> {
                    final Price existingPrice = Price.retrieve(priceCid);
                    return existingPrice.update(inactive());
                },
                "Price making inactive failed for '%s'", priceCid);
    }

    public static ResponseEntity<AhResponse<Price>> buildPriceResponse(Price price, String msg) {
        final StripeResponse lastResponse = price.getLastResponse();
        if (lastResponse.code() == HttpStatus.OK.value()) {
            price.setLastResponse(null);
            return AhResponse.buildOk(price);
        }
        return ahResponseError(msg, lastResponse.code(), price);
    }

    public static ResponseEntity<AhResponse<Price>> buildPriceCollectionResponse(PriceCollection priceCollection) {
        try {
            final StripeResponse lastResponse = priceCollection.getLastResponse();
            if (lastResponse.code() == HttpStatus.OK.value()) {
                return AhResponse.buildOk(priceCollection.getData());
            }
            final String msg = String.format("Error getting prices : Code %d \n%s", lastResponse.code(),
                    StripeHelper.objectToJson(priceCollection));
            log.error(msg);
            return AhResponse.internalError(msg);

        } catch (Exception e) {
            log.error("Error Fetching Price.", e);
            return AhResponse.internalError(e);
        }
    }
}