package ah.rest;

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;

@Getter @Builder
public class AhError {
    private @NotNull final HttpStatus status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private @NotNull final LocalDateTime timestamp;

    private @NotNull final String message;

    @JsonIgnore
    private final Throwable cause;

    public String getDebugMessage() {
        return null != cause ? cause.getLocalizedMessage() : "No exception captured";
    }

    @JsonProperty("statusValue")
    public int getStatusValue() {
        return status.value();
    }

    public static class AhErrorBuilder {
        private @NotNull LocalDateTime timestamp = LocalDateTime.now();    
    }

}
