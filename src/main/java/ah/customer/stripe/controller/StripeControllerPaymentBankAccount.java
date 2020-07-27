package ah.customer.stripe.controller;

import ah.config.StripeConfig;
import ah.helper.StripeHelper;
import ah.rest.AhResponse;
import com.stripe.Stripe;
import com.stripe.model.BankAccount;
import com.stripe.model.PaymentSourceCollection;
import com.stripe.net.StripeResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static ah.helper.HelperBankAccount.*;
import static ah.helper.StripeRequestHelper.ahResponseError;

@RestController
@RequestMapping("/api/v1/bankAccounts")
@Slf4j
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
