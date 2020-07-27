package ah.helper;

import ah.rest.AhResponse;
import com.google.gson.Gson;
import com.stripe.model.Customer;
import com.stripe.model.CustomerCollection;
import com.stripe.net.StripeResponse;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerListParams;
import com.stripe.param.CustomerUpdateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static ah.customer.stripe.StripeParam.*;
import static ah.helper.StripeHelper.runReturnOrThrow;
import static ah.helper.StripeRequestHelper.ahResponseError;

@Slf4j
public class HelperCustomer {
    private HelperCustomer() {
    }

    public static CustomerCollection customersFetch(String customerListParamString) {
        return runReturnOrThrow(() -> Customer.list((CustomerListParams)
                        CUSTOMER_LIST.fromJson(customerListParamString)),
                "Customer list retrieve failed for '%s'.", customerListParamString);
    }

    public static Customer customerCreate(String customerCreateParamString) {
        return runReturnOrThrow(() ->
                        Customer.create((CustomerCreateParams) CUSTOMER_CREATE.fromJson(customerCreateParamString)),
                "Customer create failed with parameter string '%s'.", customerCreateParamString);
    }

    public static Customer customerRetrieve(String customerId) {
        return runReturnOrThrow(() -> Customer.retrieve(customerId), "Customer retrieve failed for '%s'.", customerId);
    }

    public static Customer customerUpdate(String customerId, String customerUpdateParamString) {
        return runReturnOrThrow(() ->
                        customerRetrieve(customerId).update((CustomerUpdateParams) CUSTOMER_UPDATE.fromJson(customerUpdateParamString)),
                "Customer update failed for '%s' with '%s'", customerId, customerUpdateParamString);
    }

    public static Customer customerDelete(String customerId) {
        return runReturnOrThrow(() -> customerRetrieve(customerId).delete(), "Customer delete failed for '%s'", customerId);
    }

    public static ResponseEntity<AhResponse<Customer>> buildCustomerCollection(CustomerCollection customerCollection) {
        final StripeResponse lastResponse = customerCollection.getLastResponse();
        if (lastResponse.code() == HttpStatus.OK.value()) {
            return AhResponse.buildOk(customerCollection.getData());
        }
        final String errMsg = String.format("Error getting customers : Code %d \n%s", lastResponse.code(),
                StripeHelper.objectToJson(customerCollection));
        log.error(errMsg);
        return AhResponse.internalError(errMsg);
    }

    public static ResponseEntity<AhResponse<Customer>> buildCustomer(Customer customer, String msg) {
        final StripeResponse lastResponse = customer.getLastResponse();
        if (lastResponse.code() == HttpStatus.OK.value()) {
            try {
                final Customer fetchedCustomer = StripeHelper.jsonToObject(lastResponse.body(), Customer.class);
                return AhResponse.buildOk(fetchedCustomer);
            } catch (Exception e) {
                customer.setLastResponse(null);
                return AhResponse.buildOk(customer);
            }
        }
        return ahResponseError(msg, lastResponse.code(), customer);
    }
}
