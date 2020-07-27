package ah.helper;

import ah.rest.AhResponse;
import com.google.gson.Gson;
import com.stripe.exception.StripeException;
import com.stripe.model.BankAccount;
import com.stripe.model.Customer;
import com.stripe.model.PaymentSourceCollection;
import com.stripe.net.StripeResponse;
import com.stripe.param.BankAccountUpdateOnAccountParams;
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
public class HelperBankAccount {
    private HelperBankAccount() {
    }

    public static BankAccount bankAccountCreate(String customerCid, String createParamsString) {
        return runReturnOrThrow(() -> {
                    final Customer customer = Customer.retrieve(customerCid);
                    return (BankAccount) customer.getSources().create(
                            (PaymentSourceCollectionCreateParams) BANK_ACCOUNT_CREATE.fromJson(createParamsString));
                },
                "Customer '%s': BankAccount create failed for '%s'.", customerCid, createParamsString);
    }

    public static PaymentSourceCollection bankAccountsGet(String customerCid, String getParamString) {
        return runReturnOrThrow(() -> {
                    final Customer customer = Customer.retrieve(customerCid);
                    return customer.getSources().list((PaymentSourceCollectionListParams) BANK_ACCOUNT_LIST.fromJson(getParamString));
                },
                "Customer '%s'  BankAccount get failure '%s'", customerCid, getParamString);
    }

    public static BankAccount bankAccountRetrieve(String customerCid, String bankAccountCid) {
        return runReturnOrThrow(() -> bankAccountFetchForCustomer(customerCid, bankAccountCid),
                "Customer: '%s'  BankAccount retrieve failed for '%s'.", customerCid, bankAccountCid);
    }

    public static BankAccount bankAccountUpdate(String customerCid, String bankAccountCid, String bankAccountUpdateParamString) {
        return runReturnOrThrow(() -> {
                    final BankAccount existingBankAccount = bankAccountFetchForCustomer(customerCid, bankAccountCid);
                    return existingBankAccount.update((BankAccountUpdateOnAccountParams) BANK_ACCOUNT_UPDATE.fromJson((bankAccountUpdateParamString)));
                },
                "Customer: '%s'  BankAccount updating failed for '%s'\n'%s'.", customerCid, bankAccountCid, bankAccountUpdateParamString);
    }

    public static BankAccount bankAccountDetachFromCustomer2(String customerCid, String bankAccountCid) {
        return runReturnOrThrow(() -> {
                    final BankAccount existingBankAccount = bankAccountFetchForCustomer(customerCid, bankAccountCid);
                    return existingBankAccount.delete();
                },
                "Customer: '%s'  BankAccount detaching failed for '%s'", customerCid, bankAccountCid);
    }

    public static ResponseEntity<AhResponse<BankAccount>> buildBankAccountResponse(BankAccount paymentBankAccount, String msg) {
        final StripeResponse lastResponse = paymentBankAccount.getLastResponse();
        if (lastResponse.code() == HttpStatus.OK.value()) {
            final BankAccount fetchedBankAccount = StripeHelper.jsonToObject(lastResponse.body(), BankAccount.class);
            return AhResponse.buildOk(fetchedBankAccount);
        }
        return ahResponseError(msg, lastResponse.code(), paymentBankAccount);
    }

    public static ResponseEntity<AhResponse<BankAccount>> buildBankAccountCollectionResponse(PaymentSourceCollection bankAccountCollection) {
        try {
            final StripeResponse lastResponse = bankAccountCollection.getLastResponse();
            if (lastResponse.code() == HttpStatus.OK.value()) {
                final List<BankAccount> bankAccounts =
                        bankAccountCollection.getData().stream().map(ps -> (BankAccount) ps).collect(Collectors.toList());
                return AhResponse.buildOk(bankAccounts);
            }
            final String errMsg = String.format("Error getting BankAccounts : Code %d \n%s", lastResponse.code(),
                    StripeHelper.objectToJson(bankAccountCollection));
            log.error(errMsg);
            return AhResponse.internalError(errMsg);
        } catch (Exception e) {
            log.error("Error Fetching BankAccounts.", e);
            return AhResponse.internalError(e);
        }
    }

    private static BankAccount bankAccountFetchForCustomer(String customerCid, String paymentBankAccountCid) throws StripeException {
        final Customer customer = Customer.retrieve(customerCid);
        return (BankAccount) customer.getSources().retrieve(paymentBankAccountCid);
    }
}
