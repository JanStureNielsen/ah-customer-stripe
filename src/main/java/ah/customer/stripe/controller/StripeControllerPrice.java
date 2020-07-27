package ah.customer.stripe.controller;

import static ah.helper.AhConstant.STRIPE_REST_LARGE_LIMIT;
import static ah.helper.HelperPrice.buildPriceCollectionResponse;
import static ah.helper.HelperPrice.buildPriceResponse;
import static ah.helper.HelperPrice.priceCreate;
import static ah.helper.HelperPrice.priceGet;
import static ah.helper.HelperPrice.priceInactive;
import static ah.helper.HelperPrice.priceUpdate;
import static ah.helper.HelperPrice.pricesGet;

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
import com.stripe.model.Price;

import ah.config.StripeConfig;
import ah.rest.AhResponse;

@RestController
@RequestMapping("/api/v1/prices")
public class StripeControllerPrice {

    @Autowired
    public StripeControllerPrice(StripeConfig config) {
        Stripe.apiKey = config.stripeSecretKey();
    }

    @GetMapping("/all")
    public ResponseEntity<AhResponse<Price>> getPrices() {
        return getPrices(STRIPE_REST_LARGE_LIMIT);
    }

    @GetMapping("/")
    public ResponseEntity<AhResponse<Price>> getPrices(@RequestBody String priceListParamsString) {
        return buildPriceCollectionResponse(pricesGet(priceListParamsString));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AhResponse<Price>> getPrice(@PathVariable("id") String priceCid) {
        return buildPriceResponse(priceGet(priceCid), "Error fetching Price");
    }

    @PostMapping("/")
    public ResponseEntity<AhResponse<Price>> createPrice(@RequestBody String priceCreateParamsString) {
        return buildPriceResponse(priceCreate(priceCreateParamsString), "Error creating Price");
    }

    @PutMapping("/{id}")
    public ResponseEntity<AhResponse<Price>> updatePrice(@PathVariable("id") String priceCid, @RequestBody String priceUpdateParamsString) {
        return buildPriceResponse(priceUpdate(priceCid, priceUpdateParamsString), "Error updating Price");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AhResponse<Price>> setPriceAsInactive(@PathVariable("id") String priceCid) {
        return buildPriceResponse(priceInactive(priceCid), "Error making inactive Price");
    }

}
