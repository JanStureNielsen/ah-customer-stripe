package ah.customer.stripe.controller;

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;

@Getter
public class ApiError {

	private @NotNull HttpStatus status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private @NotNull LocalDateTime timestamp;
    private @NotNull String message;
    private @NotNull String debugMessage;

    ApiError(HttpStatus status) {
        this(status, null);
    }

    ApiError(HttpStatus status, Throwable ex) {
        this(status, "Unexpected error", ex);
    }

    ApiError(HttpStatus status, String message, Throwable ex) {
        this(status, message, ex, LocalDateTime.now());
    }

    private ApiError(HttpStatus status, String message, Throwable ex, LocalDateTime timestamp) {
        this.status = status;
        this.message = message;
        this.debugMessage = null != ex ? ex.getLocalizedMessage() : "No exception captured";
    }

}
