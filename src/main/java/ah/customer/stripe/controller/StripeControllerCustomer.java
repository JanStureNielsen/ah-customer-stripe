package ah.customer.stripe.controller;

import static ah.helper.AhConstant.STRIPE_REST_LARGE_LIMIT;
import static ah.helper.StripeHelper.customerCollection;
import static ah.helper.StripeHelper.customerCreate;
import static ah.helper.StripeHelper.customerDelete;
import static ah.helper.StripeHelper.customerRetrieve;
import static ah.helper.StripeHelper.customerUpdate;
import static ah.helper.StripeRequestHelper.ahResponseError;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import com.stripe.model.Customer;
import com.stripe.model.CustomerCollection;
import com.stripe.net.StripeResponse;

import ah.config.StripeConfig;
import ah.helper.StripeHelper;
import ah.rest.AhResponse;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/customers")
@Slf4j
public class StripeControllerCustomer {

    @Autowired
    public StripeControllerCustomer(StripeConfig config) {
        Stripe.apiKey = config.stripeSecretKey();
    }

    @GetMapping("/all")
    public ResponseEntity<AhResponse<Customer>> retrieveCustomerList() {
        return retrieveCustomerList(STRIPE_REST_LARGE_LIMIT);
    }

    @GetMapping
    public ResponseEntity<AhResponse<Customer>> retrieveCustomerList(@RequestBody String listParam) {
        return buildCustomerCollection(customerCollection(listParam));
    }

    @PostMapping
    public ResponseEntity<AhResponse<Customer>> createCustomer(@RequestBody String createParam) {
        return buildCustomer(customerCreate(createParam), "Error creating customer");
    }

    @GetMapping("/{id}")
    public ResponseEntity<AhResponse<Customer>> retrieveCustomer(@PathVariable("id") String customerId) {
        return buildCustomer(customerRetrieve(customerId), "Error retrieving customer");
    }

    @PutMapping("/{id}")
    public ResponseEntity<AhResponse<Customer>> updateCustomer(@PathVariable("id") String customerId, @RequestBody String updateParam) {
        return buildCustomer(customerUpdate(customerId, updateParam), "Error updating customer");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AhResponse<Customer>> deleteCustomer(@PathVariable("id") String customerId) {
        return buildCustomer(customerDelete(customerId), "Error deleting customer");
    }

    private ResponseEntity<AhResponse<Customer>> buildCustomerCollection(CustomerCollection customerCollection) {
        final StripeResponse lastResponse = customerCollection.getLastResponse();
        if (lastResponse.code() == HttpStatus.OK.value()) {
            return AhResponse.buildOk(customerCollection.getData());
        }
        final String errMsg = String.format("Error getting customers : Code %d \n%s", lastResponse.code(),
                StripeHelper.objectToJson(customerCollection));
        log.error(errMsg);
        return AhResponse.internalError(errMsg);
    }

    private ResponseEntity<AhResponse<Customer>> buildCustomer(Customer customer, String msg) {
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
