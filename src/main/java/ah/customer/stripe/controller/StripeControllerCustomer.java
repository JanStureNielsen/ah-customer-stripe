package ah.customer.stripe.controller;

import ah.config.StripeConfig;
import ah.helper.StripeHelper;
import ah.rest.AhResponse;
import com.stripe.Stripe;
import com.stripe.model.Customer;
import com.stripe.model.CustomerCollection;
import com.stripe.net.StripeResponse;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerListParams;
import com.stripe.param.CustomerUpdateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static ah.helper.AhConstant.STRIPE_REST_LARGE_LIMIT;
import static ah.helper.StripeRequestHelper.ahResponseError;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class StripeControllerCustomer {

    @Autowired
    public StripeControllerCustomer(StripeConfig config) {
        Stripe.apiKey = config.stripeSecretKey();
    }

    @GetMapping("/customers/all")
    public ResponseEntity<AhResponse<Customer>> getCustomers() {
        return getCustomers(STRIPE_REST_LARGE_LIMIT);
    }

    @GetMapping("/customers")
    public ResponseEntity<AhResponse<Customer>> getCustomers(@RequestBody String customerListParamString) {
        try {
            final CustomerListParams customerListParams = StripeHelper.getGson().fromJson(customerListParamString, CustomerListParams.class);
            final CustomerCollection customerCollection = Customer.list(customerListParams);
            final StripeResponse lastResponse = customerCollection.getLastResponse();
            if (lastResponse.code() == HttpStatus.OK.value()) {
                return AhResponse.buildOk(customerCollection.getData());
            }
            final String errMsg = String.format("Error getting customers : Code %d \n%s", lastResponse.code(),
                    StripeHelper.objectToJson(customerCollection));
            log.error(errMsg);
            return AhResponse.internalError(errMsg);
        } catch (Exception e) {
            log.error("Error Fetching Customer.", e);
            return AhResponse.internalError(e);
        }
    }

    @GetMapping("/customer/{id}")
    public ResponseEntity<AhResponse<Customer>> getCustomer(@PathVariable("id") String customerCid) {
        try {
            final Customer customer = Customer.retrieve(customerCid);
            return buildStripeResponseCustomer(customer, "Error fetching Customer");
        } catch (Exception e) {
            log.error("Error Fetching Customer.", e);
            return AhResponse.internalError(e);
        }
    }

    @DeleteMapping("/customer/{id}")
    public ResponseEntity<AhResponse<Customer>> deleteCustomer(@PathVariable("id") String customerCid) {
        try {
            final Customer customer = Customer.retrieve(customerCid);
            final Customer deletedCustomer = customer.delete();
            return buildStripeResponseCustomer(deletedCustomer, "Error Removing Customer.");
        } catch (Exception e) {
            log.error("Error Removing Customer.", e);
            return AhResponse.internalError(e);
        }
    }

    @PostMapping("/customer")
    public ResponseEntity<AhResponse<Customer>> createCustomer(@RequestBody String customerCreateParamString) {
        try {
            final CustomerCreateParams customerCreateParams = StripeHelper.getGson().fromJson(customerCreateParamString, CustomerCreateParams.class);
            final Customer customerNew = Customer.create(customerCreateParams);
            return buildStripeResponseCustomer(customerNew, "Error Creating Customer");
        } catch (Exception e) {
            log.error("Error Creating Customer.", e);
            return AhResponse.internalError(e);
        }
    }

    @PutMapping("/customer/{id}")
    public ResponseEntity<AhResponse<Customer>> updateCustomer(@PathVariable("id") String customerCid, @RequestBody String customerUpdateParamString) {
        try {
            final CustomerUpdateParams customerUpdateParams = StripeHelper.getGson().fromJson(customerUpdateParamString, CustomerUpdateParams.class);
            final Customer existingCustomer = Customer.retrieve(customerCid);
            final Customer updatedCustomer = existingCustomer.update(customerUpdateParams);
            return buildStripeResponseCustomer(updatedCustomer, "Error Updating Customer");
        } catch (Exception e) {
            log.error("Error Updating Customer.", e);
            return AhResponse.internalError(e);
        }
    }

    private ResponseEntity<AhResponse<Customer>> buildStripeResponseCustomer(Customer customer, String msg) {
        final StripeResponse lastResponse = customer.getLastResponse();
        if (lastResponse.code() == HttpStatus.OK.value()) {
            final Customer fetchedCustomer = StripeHelper.jsonToObject(lastResponse.body(), Customer.class);
            return AhResponse.buildOk(fetchedCustomer);
        }
        return ahResponseError(msg, lastResponse.code(), customer);
    }
}
