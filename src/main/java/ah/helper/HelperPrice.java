package ah.helper;

import static ah.customer.stripe.StripeParam.PRICE_CREATE;
import static ah.customer.stripe.StripeParam.PRICE_LIST;
import static ah.customer.stripe.StripeParam.PRICE_UPDATE;
import static ah.helper.StripeHelper.inactive;
import static ah.helper.StripeHelper.runReturnOrThrow;
import static ah.rest.AhResponse.buildOk;
import static ah.rest.AhResponse.internalError;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.stripe.model.Price;
import com.stripe.model.PriceCollection;
import com.stripe.net.StripeResponse;
import com.stripe.param.PriceCreateParams;
import com.stripe.param.PriceListParams;
import com.stripe.param.PriceUpdateParams;

import ah.rest.AhResponse;
import lombok.extern.slf4j.Slf4j;

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
                    return existingPrice.update(inactive);
                },
                "Price making inactive failed for '%s'", priceCid);
    }

    public static ResponseEntity<AhResponse<Price>> buildPriceResponse(Price price, String msg) {
        final StripeResponse lastResponse = price.getLastResponse();
        if (lastResponse.code() == HttpStatus.OK.value()) {
            price.setLastResponse(null);
            return buildOk(price);
        }
        return internalError(msg, lastResponse.code(), price);
    }

    public static ResponseEntity<AhResponse<Price>> buildPriceCollectionResponse(PriceCollection priceCollection) {
        try {
            final StripeResponse lastResponse = priceCollection.getLastResponse();
            if (lastResponse.code() == HttpStatus.OK.value()) {
                return buildOk(priceCollection.getData());
            }
            final String msg = String.format("Error getting prices : Code %d \n%s", lastResponse.code(),
                    StripeHelper.objectToJson(priceCollection));
            log.error(msg);
            return internalError(msg);

        } catch (Exception e) {
            log.error("Error Fetching Price.", e);
            return internalError(e);
        }
    }
}