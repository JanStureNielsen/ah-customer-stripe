package ah.helper;

@SuppressWarnings("serial")
public class StripeGatewayException extends RuntimeException {
    public StripeGatewayException(Throwable cause) {
        super(cause);
    }

    public StripeGatewayException(Throwable cause, String format, Object... args) {
        super(String.format(cause.getMessage() + " : " + format, args), cause);
    }

}
