package ah.customer.stripe.controller;

import static ah.helper.HelperBankAccount.bankAccountCreate;
import static ah.helper.HelperBankAccount.bankAccountDetachFromCustomer2;
import static ah.helper.HelperBankAccount.bankAccountRetrieve;
import static ah.helper.HelperBankAccount.bankAccountUpdate;
import static ah.helper.HelperBankAccount.bankAccountsGet;
import static ah.helper.HelperBankAccount.buildBankAccountCollectionResponse;
import static ah.helper.HelperBankAccount.buildBankAccountResponse;

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
import com.stripe.model.BankAccount;

import ah.config.StripeConfig;
import ah.rest.AhResponse;

@RestController
@RequestMapping("/api/v1/bankAccounts")
public class StripeControllerPaymentBankAccount {

    @Autowired
    public StripeControllerPaymentBankAccount(StripeConfig config) {
        Stripe.apiKey = config.stripeSecretKey();
    }

    @PostMapping
    public ResponseEntity<AhResponse<BankAccount>> createBankAccount(
            @PathVariable("customerCid") String customerCid, @RequestBody String paymentSourceCollectionCreateParamsString) {
        return buildBankAccountResponse(bankAccountCreate(customerCid, paymentSourceCollectionCreateParamsString)
                , "Customer: '%s'  Error Creating BankAccount");
    }

    @GetMapping("/{customerCid}")
    public ResponseEntity<AhResponse<BankAccount>> getBankAccountsForCustomer(
            @PathVariable("customerCid") String customerCid, @RequestBody String paymentBankAccountListParamString) {
        return buildBankAccountCollectionResponse(bankAccountsGet(customerCid, paymentBankAccountListParamString));
    }

    @GetMapping("/{customerCid}/{paymentBankAccountCid}")
    public ResponseEntity<AhResponse<BankAccount>> getBankAccount(
            @PathVariable("customerCid") String customerCid, @PathVariable("paymentBankAccountCid") String bankAccountCid) {
        return buildBankAccountResponse(bankAccountRetrieve(customerCid, bankAccountCid), "Error fetching a bank account");
    }

    @PutMapping("/{paymentBankAccountCid}")
    public ResponseEntity<AhResponse<BankAccount>> updateBankAccount(
            @PathVariable("customerCid") String customerCid, @PathVariable("paymentBankAccountCid") String bankAccountCid,
            @RequestBody String bankAccountUpdateParamString) {

        return buildBankAccountResponse(bankAccountUpdate(customerCid, bankAccountCid, bankAccountUpdateParamString),
                "Error Updating Bank Account");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AhResponse<BankAccount>> detachBankAccountFromCustomer(
            @PathVariable("customerCid") String customerCid, @PathVariable("paymentBankAccountCid") String bankAccountCid) {
        return buildBankAccountResponse(bankAccountDetachFromCustomer2(customerCid, bankAccountCid), "Error detaching Bank Account");
    }
}
