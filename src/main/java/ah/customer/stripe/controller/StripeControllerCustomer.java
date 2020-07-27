package ah.customer.stripe.controller;

import ah.config.StripeConfig;
import ah.rest.AhResponse;
import com.stripe.Stripe;
import com.stripe.model.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static ah.helper.AhConstant.STRIPE_REST_LARGE_LIMIT;
import static ah.helper.HelperCustomer.*;

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
        return buildCustomerCollection(customersFetch(listParam));
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
}
