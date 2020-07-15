package ah.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.stripe.net.ApiResource;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Getter
public class AhResponse<T extends ApiResource> {

    private static final int httpStatusOk = HttpStatus.OK.value();

    private AhError apiError;
    private T entity;
    private List<T> entities;

    private AhResponse() {
    }

    @JsonProperty("status")
    public int getStatus() {
        return apiError != null ? apiError.getStatus().value() : httpStatusOk;
    }

    public static <T extends ApiResource> AhResponse<T> body(T entity) {
        final AhResponse<T> res = new AhResponse<>();
        res.entity = entity;
        return res;
    }

    public static <T extends ApiResource> AhResponse<T> body(List<T> entities) {
        final AhResponse<T> res = new AhResponse<>();
        res.entities = entities;
        return res;
    }

    public static <T extends ApiResource> AhResponse<T> body(T entity, AhError apiError) {
        final AhResponse<T> res = new AhResponse<>();
        res.entity = entity;
        res.apiError = apiError;
        return res;
    }

    public static <T extends ApiResource> AhResponse<T> body(AhError apiError) {
        final AhResponse<T> res = new AhResponse<>();
        res.apiError = apiError;
        return res;
    }

    public static <T extends ApiResource> ResponseEntity<AhResponse<T>> buildOk(T entity) {
        return ResponseEntity.ok().body(AhResponse.body(entity));
    }

    public static <T extends ApiResource> ResponseEntity<AhResponse<T>> buildOk(List<T> entities) {
        return ResponseEntity.ok().body(AhResponse.body(entities));
    }

    public static <T extends ApiResource> ResponseEntity<AhResponse<T>> internalError() {
        final HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(status).body(AhResponse.body(new AhError(status)));
    }

    public static <T extends ApiResource> ResponseEntity<AhResponse<T>> internalError(Exception e) {
        final HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(status).body(AhResponse.body(new AhError(status, e)));
    }

    public static <T extends ApiResource> ResponseEntity<AhResponse<T>> internalError(String msg) {
        final HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(status).body(AhResponse.body(new AhError(status, msg)));
    }

    // Can not delete because of some chid record.
    public static <T extends ApiResource> ResponseEntity<AhResponse<T>> conflictError(String message, Exception e) {
        final HttpStatus status = HttpStatus.CONFLICT;
        return ResponseEntity.status(status).body(AhResponse.body(new AhError(status, message, e)));
    }
}
