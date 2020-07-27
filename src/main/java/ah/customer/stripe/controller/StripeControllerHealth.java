package ah.customer.stripe.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/health")
@Slf4j
public class StripeControllerHealth {

    @GetMapping
    public ResponseEntity<String> getHealth() {
        try {
            return ResponseEntity.ok().body("OK");
        } catch (Exception e) {
            log.error("Health Error:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
