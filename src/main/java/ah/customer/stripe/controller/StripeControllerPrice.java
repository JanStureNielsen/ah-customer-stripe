package ah.customer.stripe.controller;

import ah.config.StripeConfig;
import ah.helper.StripeHelper;
import ah.rest.AhResponse;
import com.stripe.Stripe;
import com.stripe.model.Price;
import com.stripe.model.PriceCollection;
import com.stripe.net.StripeResponse;
import com.stripe.param.PriceCreateParams;
import com.stripe.param.PriceListParams;
import com.stripe.param.PriceUpdateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static ah.helper.AhConstant.STRIPE_REST_LARGE_LIMIT;
import static ah.helper.StripeRequestHelper.ahResponseError;

@RestController
@RequestMapping("/api/v1/")
@Slf4j
public class StripeControllerPrice {

    @Autowired
    public StripeControllerPrice(StripeConfig config) {
        Stripe.apiKey = config.stripeSecretKey();
    }

    @GetMapping("/prices/all")
    public ResponseEntity<AhResponse<Price>> getPrices() {
        return getPrices(STRIPE_REST_LARGE_LIMIT);
    }

    @GetMapping("/prices")
    public ResponseEntity<AhResponse<Price>> getPrices(@RequestBody String priceListParamsString) {
        try {
            final PriceListParams priceListParams = StripeHelper.getGson().fromJson(priceListParamsString, PriceListParams.class);
            final PriceCollection priceCollection = Price.list(priceListParams);

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

    @GetMapping("/price/{id}")
    public ResponseEntity<AhResponse<Price>> getPrice(@PathVariable("id") String priceCid) {
        try {
            final Price price = Price.retrieve(priceCid);
            return buildStripeResponsePrice(price, "Error fetching Price");
        } catch (Exception e) {
            log.error("Error Fetching Price.", e);
            return AhResponse.internalError(e);
        }
    }

    @PostMapping("/price")
    public ResponseEntity<AhResponse<Price>> createPrice(@RequestBody String priceCreateParamsString) {
        try {
            final PriceCreateParams priceCreateParams = StripeHelper.getGson().fromJson(priceCreateParamsString, PriceCreateParams.class);
            final Price priceNew = Price.create(priceCreateParams);
            return buildStripeResponsePrice(priceNew, "Error Creating Price");
        } catch (Exception e) {
            log.error("Error Creating Price.", e);
            return AhResponse.internalError(e);
        }
    }

    @PutMapping("/price/{id}")
    public ResponseEntity<AhResponse<Price>> updatePrice(@PathVariable("id") String priceCid, @RequestBody String priceUpdateParamsString) {
        try {
            final PriceUpdateParams priceUpdateParams = StripeHelper.getGson().fromJson(priceUpdateParamsString, PriceUpdateParams.class);
            final Price existingPrice = Price.retrieve(priceCid);
            final Price updatedPrice = existingPrice.update(priceUpdateParams);
            return buildStripeResponsePrice(updatedPrice, "Error Updating Price");
        } catch (Exception e) {
            log.error("Error Updating Price.", e);
            return AhResponse.internalError(e);
        }
    }

    @DeleteMapping("/price/{id}")
    public ResponseEntity<AhResponse<Price>> setPriceAsInactive(@PathVariable("id") String priceCid) {
        try {
            final Price existingPrice = Price.retrieve(priceCid);
            final Map<String, Object> updateMap = new HashMap<String, Object>() {{
                put("active", false);
            }};
            final Price updatedPrice = existingPrice.update(updateMap);
            return buildStripeResponsePrice(updatedPrice, "Error Updating Price");
        } catch (Exception e) {
            log.error("Error Updating Price.", e);
            return AhResponse.internalError(e);
        }
    }

    private ResponseEntity<AhResponse<Price>> buildStripeResponsePrice(Price price, String msg) {
        final StripeResponse lastResponse = price.getLastResponse();
        if (lastResponse.code() == HttpStatus.OK.value()) {
            price.setLastResponse(null);
            return AhResponse.buildOk(price);
        }
        return ahResponseError(msg, lastResponse.code(), price);
    }
}
