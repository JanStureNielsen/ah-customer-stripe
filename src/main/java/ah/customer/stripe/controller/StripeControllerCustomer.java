package ah.customer.stripe.controller;

import static ah.helper.AhConstant.STRIPE_REST_LARGE_LIMIT;
import static ah.helper.HelperCustomer.buildCustomer;
import static ah.helper.HelperCustomer.buildCustomerCollection;
import static ah.helper.HelperCustomer.customerCreate;
import static ah.helper.HelperCustomer.customerDelete;
import static ah.helper.HelperCustomer.customerGet;
import static ah.helper.HelperCustomer.customerUpdate;
import static ah.helper.HelperCustomer.customerListGet;

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
import com.stripe.model.Customer;

import ah.config.StripeConfig;
import ah.rest.AhResponse;

@RestController
@RequestMapping("/api/v1/customers")
public class StripeControllerCustomer {

    @Autowired
    public StripeControllerCustomer(StripeConfig config) {
        Stripe.apiKey = config.stripeSecretKey();
    }

    @GetMapping("/all")
    public ResponseEntity<AhResponse<Customer>> customerList() {
        return customerList(STRIPE_REST_LARGE_LIMIT);
    }

    @GetMapping
    public ResponseEntity<AhResponse<Customer>> customerList(@RequestBody String params) {
        return buildCustomerCollection(customerListGet(params));
    }

    @PostMapping
    public ResponseEntity<AhResponse<Customer>> createCustomer(@RequestBody String params) {
        return buildCustomer(customerCreate(params), "Error creating customer");
    }

    @GetMapping("/{id}")
    public ResponseEntity<AhResponse<Customer>> customer(@PathVariable("id") String customerId) {
        return buildCustomer(customerGet(customerId), "Error retrieving customer");
    }

    @PutMapping("/{id}")
    public ResponseEntity<AhResponse<Customer>> updateCustomer(@PathVariable("id") String customerId, @RequestBody String params) {
        return buildCustomer(customerUpdate(customerId, params), "Error updating customer");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AhResponse<Customer>> deleteCustomer(@PathVariable("id") String customerId) {
        return buildCustomer(customerDelete(customerId), "Error deleting customer");
    }

}
