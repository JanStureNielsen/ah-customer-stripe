package ah.customer.stripe.controller;

import static ah.helper.AhConstant.STRIPE_REST_LARGE_LIMIT;
import static ah.helper.HelperPaymentCard.buildPaymentCardCollectionResponse;
import static ah.helper.HelperPaymentCard.buildPaymentCardResponse;
import static ah.helper.HelperPaymentCard.paymentCardCreate;
import static ah.helper.HelperPaymentCard.paymentCardDetach;
import static ah.helper.HelperPaymentCard.paymentCardGet;
import static ah.helper.HelperPaymentCard.paymentCardUpdate;
import static ah.helper.HelperPaymentCard.paymentCardsGet;

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
import com.stripe.model.Card;

import ah.config.StripeConfig;
import ah.rest.AhResponse;

@RestController
@RequestMapping("/api/v1/paymentCards")
public class StripeControllerPaymentCard {

    @Autowired
    public StripeControllerPaymentCard(StripeConfig config) {
        Stripe.apiKey = config.stripeSecretKey();
    }

    @PostMapping("/{customerCid}")
    public ResponseEntity<AhResponse<Card>> createCard(
            @PathVariable("customerCid") String customerCid, @RequestBody String paymentSourceCollectionCreateParamsString) {
        return buildPaymentCardResponse(paymentCardCreate(customerCid, paymentSourceCollectionCreateParamsString),
                "Error creating payment card");
    }

    @GetMapping("/all/{customerCid}")
    public ResponseEntity<AhResponse<Card>> getAllCardsForCustomer(@PathVariable("customerCid") String customerCid) {
        return getCardsForCustomer(customerCid, STRIPE_REST_LARGE_LIMIT);
    }

    @GetMapping("/{customerCid}")
    public ResponseEntity<AhResponse<Card>> getCardsForCustomer(
            @PathVariable("customerCid") String customerCid, @RequestBody String paymentCardListParamString) {
        return buildPaymentCardCollectionResponse(paymentCardsGet(customerCid, paymentCardListParamString));
    }

    @GetMapping("/{customerCid}/{paymentCardCid}")
    public ResponseEntity<AhResponse<Card>> getCard(
            @PathVariable("customerCid") String customerCid, @PathVariable("paymentCardCid") String paymentCardCid) {
        return buildPaymentCardResponse(paymentCardGet(customerCid, paymentCardCid), "Error fetching payment card");
    }

    @PutMapping("/{paymentCardCid}")
    public ResponseEntity<AhResponse<Card>> updateCard(
            @PathVariable("customerCid") String customerCid, @PathVariable("paymentCardCid") String paymentCardCid,
            @RequestBody String cardUpdateParamsString) {
        return buildPaymentCardResponse(paymentCardUpdate(customerCid, paymentCardCid, cardUpdateParamsString),
                "Error updating payment card");
    }

    @DeleteMapping("/detach/{id}")
    public ResponseEntity<AhResponse<Card>> detachCardFromCustomer(
            @PathVariable("customerCid") String customerCid, @PathVariable("paymentCardCid") String paymentCardCid) {
        return buildPaymentCardResponse(paymentCardDetach(customerCid, paymentCardCid),
                "Error detaching payment card");
    }
}