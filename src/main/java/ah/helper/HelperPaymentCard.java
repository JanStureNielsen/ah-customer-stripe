package ah.helper;

import ah.rest.AhResponse;
import com.stripe.exception.StripeException;
import com.stripe.model.Card;
import com.stripe.model.Customer;
import com.stripe.model.PaymentSourceCollection;
import com.stripe.net.StripeResponse;
import com.stripe.param.CardUpdateOnAccountParams;
import com.stripe.param.PaymentSourceCollectionCreateParams;
import com.stripe.param.PaymentSourceCollectionListParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.stream.Collectors;

import static ah.customer.stripe.StripeParam.*;
import static ah.helper.StripeHelper.runReturnOrThrow;
import static ah.helper.StripeRequestHelper.ahResponseError;

@Slf4j
public class HelperPaymentCard {
    private HelperPaymentCard() {
    }

    public static Card paymentCardCreate(String customerCid, String paymentCardCreateParamsString) {
        return runReturnOrThrow(() -> {
            final Customer customer = Customer.retrieve(customerCid);
            return (Card) customer.getSources().create((PaymentSourceCollectionCreateParams)
                    PAYMENT_CARD_CREATE.fromJson(paymentCardCreateParamsString));
        }, "PaymentCard create failed for '%s'", paymentCardCreateParamsString);
    }

    public static PaymentSourceCollection paymentCardsGet(String customerCid, String paymentCardListParamString) {
        return runReturnOrThrow(() -> {
            final Customer customer = Customer.retrieve(customerCid);
            return customer.getSources().list((PaymentSourceCollectionListParams)
                    PAYMENT_CARD_LIST.fromJson(paymentCardListParamString));
        }, "PaymentCards fetch failed for '%s'", paymentCardListParamString);
    }

    public static Card paymentCardGet(String customerCid, String paymentCardCid) {
        return runReturnOrThrow(() -> fetchCardFromCustomer(customerCid, paymentCardCid),
                "PaymentCard fetch failed for '%s' '%s'", customerCid, paymentCardCid);
    }

    public static Card paymentCardUpdate(String customerCid, String paymentCardCid, String cardUpdateParamsString) {
        return runReturnOrThrow(() -> {
            final Card existingCard = fetchCardFromCustomer(customerCid, paymentCardCid);
            return (Card) existingCard.update((CardUpdateOnAccountParams)
                    PAYMENT_CARD_UPDATE.fromJson(cardUpdateParamsString));
        }, "PaymentCard update failed for '%s'", cardUpdateParamsString);
    }

    public static Card paymentCardDetach(String customerCid, String paymentCardCid) {
        return runReturnOrThrow(() -> {
            final Card existingCard = fetchCardFromCustomer(customerCid, paymentCardCid);
            return existingCard.delete();
        }, "PaymentCard delete failed for '%s' '%s'", customerCid, paymentCardCid);
    }

    private static Card fetchCardFromCustomer(String customerCid, String paymentCardCid) throws StripeException {
        final Customer customer = Customer.retrieve(customerCid);
        return (Card) customer.getSources().retrieve(paymentCardCid);
    }

    public static ResponseEntity<AhResponse<Card>> buildPaymentCardResponse(Card paymentCard, String msg) {
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

    public static ResponseEntity<AhResponse<Card>> buildPaymentCardCollectionResponse(PaymentSourceCollection paymentCardCollection) {
        try {
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
}
