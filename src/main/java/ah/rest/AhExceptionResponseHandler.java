package ah.rest;

import org.slf4j.event.Level;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import ah.helper.StripeGatewayException;

@ControllerAdvice
public class AhExceptionResponseHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = { StripeGatewayException.class })
    protected ResponseEntity<Object> handleStripeGatewayException(StripeGatewayException cause, WebRequest request) {
        return handle(
            HttpStatus.NOT_FOUND,
            cause.getCause().getClass().getName(),
            cause.getLocalizedMessage(),
            Level.INFO,
            cause,
            request
        );
    }

    private ResponseEntity<Object> handle(HttpStatus status, String type, String message, Level level, Throwable cause, WebRequest request) {
        return responseEntityHandler(
                AhError.builder()
                    .status(status)
                    .message(message)
                    .cause(cause)
                    .build(),
                request
                );
    }

    private ResponseEntity<Object> responseEntityHandler(AhError failure, WebRequest request) {
        return handleExceptionInternal(
            (Exception) failure.getCause(),
            failure,
            new HttpHeaders(),
            failure.getStatus(),
            request
        );
    }

}
