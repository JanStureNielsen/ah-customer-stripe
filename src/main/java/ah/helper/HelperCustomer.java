package ah.helper;

import static ah.customer.stripe.StripeParam.CUSTOMER_CREATE;
import static ah.customer.stripe.StripeParam.CUSTOMER_LIST;
import static ah.customer.stripe.StripeParam.CUSTOMER_UPDATE;
import static ah.helper.StripeHelper.runReturnOrThrow;
import static ah.rest.AhResponse.buildOk;
import static ah.rest.AhResponse.internalError;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.stripe.model.Customer;
import com.stripe.model.CustomerCollection;
import com.stripe.net.StripeResponse;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerListParams;
import com.stripe.param.CustomerUpdateParams;

import ah.rest.AhResponse;

public class HelperCustomer {
    private HelperCustomer() {
    }

    public static CustomerCollection customerListGet(String customerListParamString) {
        return runReturnOrThrow(() -> Customer.list((CustomerListParams)
                        CUSTOMER_LIST.fromJson(customerListParamString)),
                "Customer list retrieve failed for '%s'.", customerListParamString);
    }

    public static Customer customerCreate(String customerCreateParamString) {
        return runReturnOrThrow(() ->
                        Customer.create((CustomerCreateParams) CUSTOMER_CREATE.fromJson(customerCreateParamString)),
                "Customer create failed with parameter string '%s'.", customerCreateParamString);
    }

    public static Customer customerGet(String customerId) {
        return runReturnOrThrow(() -> Customer.retrieve(customerId), "Customer retrieve failed for '%s'.", customerId);
    }

    public static Customer customerUpdate(String customerId, String customerUpdateParamString) {
        return runReturnOrThrow(() ->
                        customerGet(customerId).update((CustomerUpdateParams) CUSTOMER_UPDATE.fromJson(customerUpdateParamString)),
                "Customer update failed for '%s' with '%s'", customerId, customerUpdateParamString);
    }

    public static Customer customerDelete(String customerId) {
        return runReturnOrThrow(() -> customerGet(customerId).delete(), "Customer delete failed for '%s'", customerId);
    }

    public static ResponseEntity<AhResponse<Customer>> buildCustomerCollection(CustomerCollection customerCollection) {
        final StripeResponse lastResponse = customerCollection.getLastResponse();
        if (lastResponse.code() == HttpStatus.OK.value()) {
            return buildOk(customerCollection.getData());
        }
        return internalError(String.format("Error getting customers : Code %d \n%s", lastResponse.code(),
                StripeHelper.objectToJson(customerCollection)));
    }

    public static ResponseEntity<AhResponse<Customer>> buildCustomer(Customer customer, String msg) {
        final StripeResponse lastResponse = customer.getLastResponse();
        if (lastResponse.code() == HttpStatus.OK.value()) {
            try {
                final Customer fetchedCustomer = StripeHelper.jsonToObject(lastResponse.body(), Customer.class);
                return buildOk(fetchedCustomer);
            } catch (Exception e) {
                customer.setLastResponse(null);
                return buildOk(customer);
            }
        }
        return internalError(msg, lastResponse.code(), customer);
    }

}
