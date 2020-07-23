package ah.helper;

import ah.rest.AhResponse;
import com.stripe.net.ApiResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

@Slf4j
public class StripeRequestHelper {
    private StripeRequestHelper() {
    }

    public static <T extends ApiResource> ResponseEntity<AhResponse<T>> ahResponseError(String msg, int httpStatus, T entity) {
        final String errMsg = String.format("%s : Code %d \n%s", msg, httpStatus, StripeHelper.objectToJson(entity));
        log.error(errMsg);
        return AhResponse.internalError(errMsg);
    }

}
