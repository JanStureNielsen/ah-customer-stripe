package ah.rest;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.stripe.net.ApiResource;

import ah.helper.StripeHelper;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter @Builder @Slf4j
public class AhResponse<T extends ApiResource> {
    private static final int httpStatusOk = HttpStatus.OK.value();

    private final AhError apiError;
    private final T entity;
    private final List<T> entities;

    @JsonProperty("status")
    public int getStatus() {
        return apiError != null ? apiError.getStatusValue() : httpStatusOk;
    }

    public static <T extends ApiResource> AhResponse<T> body(T entity) {
        return AhResponse.<T>builder().entity(entity).build();
    }

    public static <T extends ApiResource> AhResponse<T> body(List<T> entities) {
        return AhResponse.<T>builder().entities(entities).build();
    }

    public static <T extends ApiResource> AhResponse<T> body(T entity, AhError apiError) {
        return AhResponse.<T>builder().apiError(apiError).entity(entity).build();
    }

    public static <T extends ApiResource> AhResponse<T> body(AhError apiError) {
        return AhResponse.<T>builder().apiError(apiError).build();
    }

    public static <T extends ApiResource> ResponseEntity<AhResponse<T>> buildOk(T entity) {
        return ResponseEntity.ok().body(AhResponse.body(entity));
    }

    public static <T extends ApiResource> ResponseEntity<AhResponse<T>> buildOk(List<T> entities) {
        return ResponseEntity.ok().body(AhResponse.body(entities));
    }

    public static <T extends ApiResource> ResponseEntity<AhResponse<T>> internalError() {
        final HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(status).body(AhResponse.body(AhError.builder().status(status).build()));
    }

    public static <T extends ApiResource> ResponseEntity<AhResponse<T>> internalError(Exception e) {
        final HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(status).body(AhResponse.body(AhError.builder().status(status).cause(e).build()));
    }

    public static <T extends ApiResource> ResponseEntity<AhResponse<T>> internalError(String msg) {
        final HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(status).body(AhResponse.body(AhError.builder().status(status).message(msg).build()));
    }

    public static <T extends ApiResource> ResponseEntity<AhResponse<T>> internalError(String msg, Exception cause) {
        final HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(status).body(AhResponse.body(AhError.builder().status(status).message(msg).cause(cause).build()));
    }

    // Can not delete because of some child record.
    public static <T extends ApiResource> ResponseEntity<AhResponse<T>> conflictError(String message, Exception e) {
        final HttpStatus status = HttpStatus.CONFLICT;
        return ResponseEntity.status(status).body(AhResponse.body(AhError.builder().status(status).message(message).cause(e).build()));
    }

	public static <T extends ApiResource> ResponseEntity<AhResponse<T>> internalError(String msg, int httpStatus, T entity) {
	    final String errMsg = String.format("%s : Code %d \n%s", msg, httpStatus, StripeHelper.objectToJson(entity));
	    log.error(errMsg);
	    return AhResponse.internalError(errMsg);
	}

}
