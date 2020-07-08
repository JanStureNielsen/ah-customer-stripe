package ah.customer.stripe.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class StripeConfig {

    @Value("${stripe.sk}")
    private String stripeSecretKey;

    public String stripeSecretKey() {
        return stripeSecretKey;
    }

}
