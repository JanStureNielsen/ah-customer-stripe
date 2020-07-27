package ah.helper;

import com.stripe.exception.StripeException;

@FunctionalInterface
public interface AhStripeSupplier<E> {
    public E get() throws StripeException;
}
