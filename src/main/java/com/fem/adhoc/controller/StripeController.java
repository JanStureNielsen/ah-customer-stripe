package com.fem.adhoc.controller;

import com.fem.adhoc.config.StripeConfig;
import com.fem.adhoc.util.StripeHelper;
import com.stripe.Stripe;
import com.stripe.model.Customer;
import com.stripe.net.StripeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

//@Api(value = "", description = "Interfaces to the Stripe.com system.")
@RestController
@RequestMapping("/api")
public class StripeController {
    final Logger logger = LoggerFactory.getLogger(StripeController.class);

    @Autowired
    public StripeController(StripeConfig config) {
        Stripe.apiKey = config.stripeSecretKey();
    }

    @GetMapping("/health")
    public ResponseEntity<String> getHealth() {
        try {
            return ResponseEntity.ok().body("OK");
        } catch (Exception e) {
            logger.error("Error:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/customer/{id}")
    public ResponseEntity<Customer> getCustomer(@PathVariable("id") String customerCid) {  // Cid is Character ID
        try {
            final Customer customer = Customer.retrieve(customerCid);
            return returnStripeResponseCustomer(customer, "Error fetching Customer");
        } catch (Exception e) {
            logger.error("Error Fetching Customer.", e);
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
            logger.error("Error Removing Customer.", e);
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
            logger.error("Error Creating Customer.", e);
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
            logger.error("Error Creating Customer.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private ResponseEntity<Customer> returnStripeResponseCustomer(Customer customer, String msg) {
        final StripeResponse lastResponse = customer.getLastResponse();
        if (lastResponse.code() == HttpStatus.OK.value()) {
            final Customer fetchedCustomer = StripeHelper.jsonToObject(lastResponse.body(), Customer.class);
            return ResponseEntity.ok().body(fetchedCustomer);
        }
        logger.error(String.format("%s (alsk) : Code %d \n%s", lastResponse.code(),
                StripeHelper.objectToJson(customer)));

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    // Example of a convenience method and functional programming.
    private <T> ResponseEntity<T> runAndReturn(Supplier<T> supplier, String msg) {
        try {
            return ResponseEntity.ok().body(supplier.get());
        } catch (Exception e) {
            logger.error("Error:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
