package ah.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
class AhValidationError extends AhSubError {
    private String object;
    private String field;
    private Object rejectedValue;
    private String message;

    AhValidationError(String object, String message) {
        this.object = object;
        this.message = message;
    }
}