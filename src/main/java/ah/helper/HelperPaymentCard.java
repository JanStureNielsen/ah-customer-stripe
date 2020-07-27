package ah.helper;

import static ah.customer.stripe.StripeParam.PAYMENT_CARD_CREATE;
import static ah.customer.stripe.StripeParam.PAYMENT_CARD_LIST;
import static ah.customer.stripe.StripeParam.PAYMENT_CARD_UPDATE;
import static ah.helper.StripeHelper.runReturnOrThrow;
import static ah.rest.AhResponse.buildOk;
import static ah.rest.AhResponse.internalError;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.stripe.exception.StripeException;
import com.stripe.model.Card;
import com.stripe.model.Customer;
import com.stripe.model.PaymentSourceCollection;
import com.stripe.net.StripeResponse;
import com.stripe.param.CardUpdateOnAccountParams;
import com.stripe.param.PaymentSourceCollectionCreateParams;
import com.stripe.param.PaymentSourceCollectionListParams;

import ah.rest.AhResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HelperPaymentCard {
    private HelperPaymentCard() {
    }

    public static Card cardCreate(String customerId, String params) {
        return runReturnOrThrow(() -> _cardCreate(customerId, params), "Card create failed for '%s' with '%s'.", customerId, params);
    }

    public static Card cardGet(String customerId, String cardId) {
        return runReturnOrThrow(() -> _cardGet(customerId, cardId), "Card read failed for '%s' '%s'.", customerId, cardId);
    }

    public static PaymentSourceCollection cardListGet(String customerId, String params) {
        return runReturnOrThrow(() -> _cardCollection(customerId, params), "Card collection read failed for '%s' with '%s'.", customerId, params);
    }

    public static Card cardUpdate(String customerId, String cardId, String params) {
        return runReturnOrThrow(() -> _cardUpdate(customerId, cardId, params), "Card update failed for '%s' with '%s'.", customerId, params);
    }

    public static Card cardDelete(String customerId, String cardId) {
        return runReturnOrThrow(() -> _cardDelete(customerId, cardId), "Card delete failed for '%s' '%s'.", customerId, cardId);
    }

    public static ResponseEntity<AhResponse<Card>> buildPaymentCardResponse(Card paymentCard, String msg) {
        final StripeResponse lastResponse = paymentCard.getLastResponse();
        if (lastResponse.code() == HttpStatus.OK.value()) {
            try {
                final Card fetchedCard = StripeHelper.jsonToObject(lastResponse.body(), Card.class);
                return buildOk(fetchedCard);
            } catch (Exception e) {
                paymentCard.setLastResponse(null);
                return buildOk(paymentCard);
            }
        }
        return internalError(msg, lastResponse.code(), paymentCard);
    }

    public static ResponseEntity<AhResponse<Card>> buildPaymentCardCollectionResponse(PaymentSourceCollection paymentCardCollection) {
        try {
            final StripeResponse lastResponse = paymentCardCollection.getLastResponse();
            if (lastResponse.code() == HttpStatus.OK.value()) {
                final List<Card> cards =
                        paymentCardCollection.getData().stream().map(ps -> (Card) ps).collect(Collectors.toList());
                return buildOk(cards);
            }
            final String errMsg = String.format("Error getting Cards : Code %d \n%s", lastResponse.code(),
                    StripeHelper.objectToJson(paymentCardCollection));
            log.error(errMsg);
            return internalError(errMsg);
        } catch (Exception e) {
            log.error("Error Fetching Card.", e);
            return internalError(e);
        }

    }

    private static PaymentSourceCollection _sources(String customerId) throws StripeException {
        return Customer.retrieve(customerId).getSources();
    }

    private static PaymentSourceCollection _cardCollection(String customerId, String param) throws StripeException {
        return _sources(customerId).list((PaymentSourceCollectionListParams) PAYMENT_CARD_LIST.fromJson(param));
    }

    private static Card _cardGet(String customerId, String cardId) throws StripeException {
        return (Card) _sources(customerId).retrieve(cardId);
    }

    private static Card _cardCreate(String customerId, String param) throws StripeException {
        return (Card) _sources(customerId).create((PaymentSourceCollectionCreateParams) PAYMENT_CARD_CREATE.fromJson(param));
    }

    private static Card _cardUpdate(String customerId, String cardId, String params) throws StripeException {
        return _cardGet(customerId, cardId).update((CardUpdateOnAccountParams) PAYMENT_CARD_UPDATE.fromJson(params));
    }

    private static Card _cardDelete(String customerId, String cardId) throws StripeException {
        return _cardGet(customerId, cardId).delete();
    }

}
