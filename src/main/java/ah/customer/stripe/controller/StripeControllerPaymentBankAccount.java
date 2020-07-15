package ah.customer.stripe.controller;

import ah.config.StripeConfig;
import ah.helper.StripeHelper;
import ah.rest.AhResponse;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.BankAccount;
import com.stripe.model.Customer;
import com.stripe.model.PaymentSourceCollection;
import com.stripe.net.StripeResponse;
import com.stripe.param.BankAccountUpdateOnAccountParams;
import com.stripe.param.PaymentSourceCollectionCreateParams;
import com.stripe.param.PaymentSourceCollectionListParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static ah.helper.StripeRequestHelper.ahResponseError;

@RestController
@RequestMapping("/api/v1/")
@Slf4j
public class StripeControllerPaymentBankAccount {

    @Autowired
    public StripeControllerPaymentBankAccount(StripeConfig config) {
        Stripe.apiKey = config.stripeSecretKey();
    }

    @GetMapping("/paymentBankAccounts/{customerCid}")
    public ResponseEntity<AhResponse<BankAccount>> getBankAccountsForCustomer(
            @PathVariable("customerCid") String customerCid, @RequestBody String paymentBankAccountListParamString) {
        try {
            final Customer customer = Customer.retrieve(customerCid);
            final PaymentSourceCollectionListParams paymentBankAccountListParams =
                    StripeHelper.getGson().fromJson(paymentBankAccountListParamString, PaymentSourceCollectionListParams.class);

            final PaymentSourceCollection paymentBankAccountCollection = customer.getSources().list(paymentBankAccountListParams);
            final StripeResponse lastResponse = paymentBankAccountCollection.getLastResponse();
            if (lastResponse.code() == HttpStatus.OK.value()) {
                final List<BankAccount> bankAccounts =
                        paymentBankAccountCollection.getData().stream().map(ps -> (BankAccount) ps).collect(Collectors.toList());
                return AhResponse.buildOk(bankAccounts);
            }
            final String errMsg = String.format("Error getting BankAccounts : Code %d \n%s", lastResponse.code(),
                    StripeHelper.objectToJson(paymentBankAccountCollection));
            log.error(errMsg);
            return AhResponse.internalError(errMsg);
        } catch (Exception e) {
            log.error("Error Fetching BankAccount.", e);
            return AhResponse.internalError(e);
        }
    }

    @GetMapping("/paymentBankAccount/{customerCid}/{paymentBankAccountCid}")
    public ResponseEntity<AhResponse<BankAccount>> getBankAccount(
            @PathVariable("customerCid") String customerCid, @PathVariable("paymentBankAccountCid") String paymentBankAccountCid) {
        try {
            final BankAccount paymentBankAccount = fetchBankAccountFromCustomer(customerCid, paymentBankAccountCid);
            return buildStripeResponseBankAccount(paymentBankAccount, "Error fetching BankAccount");
        } catch (Exception e) {
            log.error("Error Fetching BankAccount.", e);
            return AhResponse.internalError(e);
        }
    }

    @PostMapping("/paymentBankAccount")
    public ResponseEntity<AhResponse<BankAccount>> createBankAccount(
            @PathVariable("customerCid") String customerCid, @RequestBody String paymentSourceCollectionCreateParamsString) {
        try {
            final PaymentSourceCollectionCreateParams paymentBankAccountCreateParams =
                    StripeHelper.getGson().fromJson(paymentSourceCollectionCreateParamsString, PaymentSourceCollectionCreateParams.class);
            final Customer customer = Customer.retrieve(customerCid);

            final BankAccount paymentBankAccountNew = (BankAccount) customer.getSources().create(paymentBankAccountCreateParams);
            return buildStripeResponseBankAccount(paymentBankAccountNew, "Error Creating BankAccount");
        } catch (Exception e) {
            log.error("Error Creating BankAccount.", e);
            return AhResponse.internalError(e);
        }
    }

    @PutMapping("/paymentBankAccount/{paymentBankAccountCid}")
    public ResponseEntity<AhResponse<BankAccount>> updateBankAccount(
            @PathVariable("customerCid") String customerCid, @PathVariable("paymentBankAccountCid") String paymentBankAccountCid,
            @RequestBody String bankAccountUpdateParamsString) {
        try {
            final BankAccountUpdateOnAccountParams bankAccountUpdateOnAccountParams =
                    StripeHelper.getGson().fromJson(bankAccountUpdateParamsString, BankAccountUpdateOnAccountParams.class);

            final BankAccount existingBankAccount = fetchBankAccountFromCustomer(customerCid, paymentBankAccountCid);
            final BankAccount updatedBankAccount = existingBankAccount.update(bankAccountUpdateOnAccountParams);
            return buildStripeResponseBankAccount(updatedBankAccount, "Error Updating BankAccount");
        } catch (Exception e) {
            log.error("Error Updating BankAccount.", e);
            return AhResponse.internalError(e);
        }
    }

    @DeleteMapping("/paymentBankAccount/{id}")
    public ResponseEntity<AhResponse<BankAccount>> detachBankAccountFromCustomer(
            @PathVariable("customerCid") String customerCid, @PathVariable("paymentBankAccountCid") String paymentBankAccountCid) {
        try {
            final BankAccount existingBankAccount = fetchBankAccountFromCustomer(customerCid, paymentBankAccountCid);
            final BankAccount deletedBankAccount = existingBankAccount.delete();
            return buildStripeResponseBankAccount(deletedBankAccount, "Error Deleting BankAccount.");
        } catch (Exception e) {
            log.error("Error Removing BankAccount.", e);
            return AhResponse.internalError(e);
        }
    }

    private BankAccount fetchBankAccountFromCustomer(String customerCid, String paymentBankAccountCid) throws StripeException {
        final Customer customer = Customer.retrieve(customerCid);
        return (BankAccount) customer.getSources().retrieve(paymentBankAccountCid);
    }

    private ResponseEntity<AhResponse<BankAccount>> buildStripeResponseBankAccount(BankAccount paymentBankAccount, String msg) {
        final StripeResponse lastResponse = paymentBankAccount.getLastResponse();
        if (lastResponse.code() == HttpStatus.OK.value()) {
            final BankAccount fetchedBankAccount = StripeHelper.jsonToObject(lastResponse.body(), BankAccount.class);
            return AhResponse.buildOk(fetchedBankAccount);
        }
        return ahResponseError(msg, lastResponse.code(), paymentBankAccount);
    }
}
