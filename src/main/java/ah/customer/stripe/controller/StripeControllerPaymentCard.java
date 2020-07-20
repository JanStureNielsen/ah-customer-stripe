package ah.customer.stripe.controller;

import ah.config.StripeConfig;
import ah.helper.StripeHelper;
import ah.rest.AhResponse;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Card;
import com.stripe.model.Customer;
import com.stripe.model.PaymentSourceCollection;
import com.stripe.net.StripeResponse;
import com.stripe.param.CardUpdateOnAccountParams;
import com.stripe.param.PaymentSourceCollectionCreateParams;
import com.stripe.param.PaymentSourceCollectionListParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static ah.helper.AhConstant.STRIPE_REST_LARGE_LIMIT;
import static ah.helper.StripeRequestHelper.ahResponseError;

@RestController
@RequestMapping("/api/v1/")
@Slf4j
public class StripeControllerPaymentCard {

    @Autowired
    public StripeControllerPaymentCard(StripeConfig config) {
        Stripe.apiKey = config.stripeSecretKey();
    }

    @PostMapping("/paymentCard/{customerCid}")
    public ResponseEntity<AhResponse<Card>> createCard(
            @PathVariable("customerCid") String customerCid, @RequestBody String paymentSourceCollectionCreateParamsString) {
        try {
            final PaymentSourceCollectionCreateParams paymentCardCreateParams =
                    StripeHelper.getGson().fromJson(paymentSourceCollectionCreateParamsString, PaymentSourceCollectionCreateParams.class);
            final Customer customer = Customer.retrieve(customerCid);

            final Card paymentCardNew = (Card) customer.getSources().create(paymentCardCreateParams);
            return buildStripeResponseCard(paymentCardNew, "Error Creating Card");
        } catch (Exception e) {
            log.error("Error Creating Card.", e);
            return AhResponse.internalError(e);
        }
    }

    @GetMapping("/paymentCards/all/{customerCid}")
    public ResponseEntity<AhResponse<Card>> getAllCardsForCustomer(@PathVariable("customerCid") String customerCid) {
        return getCardsForCustomer(customerCid, STRIPE_REST_LARGE_LIMIT);
    }

    @GetMapping("/paymentCards/{customerCid}")
    public ResponseEntity<AhResponse<Card>> getCardsForCustomer(
            @PathVariable("customerCid") String customerCid, @RequestBody String paymentCardListParamString) {
        try {
            final Customer customer = Customer.retrieve(customerCid);
            final PaymentSourceCollectionListParams paymentCardListParams =
                    StripeHelper.getGson().fromJson(paymentCardListParamString, PaymentSourceCollectionListParams.class);

            final PaymentSourceCollection paymentCardCollection = customer.getSources().list(paymentCardListParams);
            final StripeResponse lastResponse = paymentCardCollection.getLastResponse();
            if (lastResponse.code() == HttpStatus.OK.value()) {
                final List<Card> cards =
                        paymentCardCollection.getData().stream().map(ps -> (Card) ps).collect(Collectors.toList());
                return AhResponse.buildOk(cards);
            }
            final String errMsg = String.format("Error getting Cards : Code %d \n%s", lastResponse.code(),
                    StripeHelper.objectToJson(paymentCardCollection));
            log.error(errMsg);
            return AhResponse.internalError(errMsg);
        } catch (Exception e) {
            log.error("Error Fetching Card.", e);
            return AhResponse.internalError(e);
        }
    }

    @GetMapping("/paymentCard/{customerCid}/{paymentCardCid}")
    public ResponseEntity<AhResponse<Card>> getCard(
            @PathVariable("customerCid") String customerCid, @PathVariable("paymentCardCid") String paymentCardCid) {
        try {
            final Card paymentCard = fetchCardFromCustomer(customerCid, paymentCardCid);
            return buildStripeResponseCard(paymentCard, "Error fetching Card");
        } catch (Exception e) {
            log.error("Error Fetching Card.", e);
            return AhResponse.internalError(e);
        }
    }

    @PutMapping("/paymentCard/{paymentCardCid}")
    public ResponseEntity<AhResponse<Card>> updateCard(
            @PathVariable("customerCid") String customerCid, @PathVariable("paymentCardCid") String paymentCardCid,
            @RequestBody String cardUpdateParamsString) {
        try {
            final CardUpdateOnAccountParams cardUpdateOnAccountParams =
                    StripeHelper.getGson().fromJson(cardUpdateParamsString, CardUpdateOnAccountParams.class);

            final Card existingCard = fetchCardFromCustomer(customerCid, paymentCardCid);
            final Card updatedCard = existingCard.update(cardUpdateOnAccountParams);
            return buildStripeResponseCard(updatedCard, "Error Updating Card");
        } catch (Exception e) {
            log.error("Error Updating Card.", e);
            return AhResponse.internalError(e);
        }
    }

    @DeleteMapping("/paymentCard/detach/{id}")
    public ResponseEntity<AhResponse<Card>> detachCardFromCustomer(
            @PathVariable("customerCid") String customerCid, @PathVariable("paymentCardCid") String paymentCardCid) {
        try {
            final Card existingCard = fetchCardFromCustomer(customerCid, paymentCardCid);
            final Card deletedCard = existingCard.delete();
            return buildStripeResponseCard(deletedCard, "Error Deleting Card.");
        } catch (Exception e) {
            log.error("Error Removing Card.", e);
            return AhResponse.internalError(e);
        }
    }

    private Card fetchCardFromCustomer(String customerCid, String paymentCardCid) throws StripeException {
        final Customer customer = Customer.retrieve(customerCid);
        return (Card) customer.getSources().retrieve(paymentCardCid);
    }

    private ResponseEntity<AhResponse<Card>> buildStripeResponseCard(Card paymentCard, String msg) {
        final StripeResponse lastResponse = paymentCard.getLastResponse();
        if (lastResponse.code() == HttpStatus.OK.value()) {
            try {
                final Card fetchedCard = StripeHelper.jsonToObject(lastResponse.body(), Card.class);
                return AhResponse.buildOk(fetchedCard);
            } catch (Exception e) {
                paymentCard.setLastResponse(null);
                return AhResponse.buildOk(paymentCard);
            }
        }
        return ahResponseError(msg, lastResponse.code(), paymentCard);
    }
}
