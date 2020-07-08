package ah.customer.stripe.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

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
import com.stripe.net.StripeResponse;

import ah.customer.stripe.config.StripeConfig;
import ah.customer.stripe.util.StripeHelper;
import lombok.extern.slf4j.Slf4j;

//@Api(value = "", description = "Interfaces to the Stripe.com system.")
@RestController
@RequestMapping("/api") @Slf4j
public class StripeController {
    @Autowired
    public StripeController(StripeConfig config) {
        Stripe.apiKey = config.stripeSecretKey();
    }

    @GetMapping("/health")
    public ResponseEntity<String> getHealth() {
        try {
            return ResponseEntity.ok().body("OK");
        } catch (Exception e) {
            log.error("Error:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/customer/{id}")
    public ResponseEntity<Customer> getCustomer(@PathVariable("id") String customerCid) {  // Cid is Character ID
        try {
            final Customer customer = Customer.retrieve(customerCid);
            return returnStripeResponseCustomer(customer, "Error fetching Customer");
        } catch (Exception e) {
            log.error("Error Fetching Customer.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/customer/{id}")
    public ResponseEntity<Customer> deleteCustomer(@PathVariable("id") String customerCid) {  // Cid is Character ID
        try {
            final Customer customer = Customer.retrieve(customerCid);
            final Customer deletedCustomer = customer.delete();
            return returnStripeResponseCustomer(deletedCustomer, "Error Removing Customer.");
        } catch (Exception e) {
            log.error("Error Removing Customer.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/customer")
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
        try {
            final Map<String, Object> customerMap = StripeHelper.objectToMap(customer);
            final Customer customerNew = Customer.create(customerMap);
            return returnStripeResponseCustomer(customerNew, "Error Creating Customer");
        } catch (Exception e) {
            log.error("Error Creating Customer.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/customer")
    public ResponseEntity<Customer> updateCustomer(@RequestBody Customer customer) {

        try {
            // Turn the input changes into a map
            final Map<String, Object> inputMetadata = StripeHelper.objectToMap(customer);
            inputMetadata.remove("id");

            // Get the current customer
            final String customerCid = customer.getId();
            final Customer existingCustomer = Customer.retrieve(customerCid);

            // Update the customer as per Stripe docs.
            //      https://stripe.com/docs/api/customers/update?lang=curl
            final Map<String, Object> params = new HashMap<>();
            params.put("metadata", inputMetadata);
            final Customer updatedCustomer = existingCustomer.update(params);

            return returnStripeResponseCustomer(updatedCustomer, "Error Updating Customer");
        } catch (Exception e) {
            log.error("Error Creating Customer.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private ResponseEntity<Customer> returnStripeResponseCustomer(Customer customer, String msg) {
        final StripeResponse lastResponse = customer.getLastResponse();
        if (lastResponse.code() == HttpStatus.OK.value()) {
            final Customer fetchedCustomer = StripeHelper.jsonToObject(lastResponse.body(), Customer.class);
            return ResponseEntity.ok().body(fetchedCustomer);
        }
        log.error(String.format("%s (alsk) : Code %d \n%s", lastResponse.code(),
                StripeHelper.objectToJson(customer)));

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    // Example of a convenience method and functional programming.
    private <T> ResponseEntity<T> runAndReturn(Supplier<T> supplier, String msg) {
        try {
            return ResponseEntity.ok().body(supplier.get());
        } catch (Exception e) {
            log.error("Error:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
