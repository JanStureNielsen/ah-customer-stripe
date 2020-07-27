package ah.helper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.stripe.exception.StripeException;

import java.util.HashMap;
import java.util.Map;

public class StripeHelper {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @SuppressWarnings("serial")
    public static final Map<String, Object> inactive = new HashMap<String, Object>() {{
        put("active", false);
    }};

    private StripeHelper() {
    }

    // TODO: make this private...
    public static Gson getGson() {
        return gson;
    }

    @SuppressWarnings("unchecked")
    public static <T> Map<String, Object> objectToMap(T entity) {
        final String jsonStr = gson.toJson(entity);
        return gson.fromJson(jsonStr, Map.class);
    }

    public static <T> String objectToJson(T entity) {
        return gson.toJson(entity);
    }

    public static <T> T jsonToObject(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    public static <E> E runReturnOrThrow(AhStripeSupplier<E> fetchFunction, String format, Object... args) {
        try {
            return fetchFunction.get();
        } catch (StripeException x) {
            throw new StripeGatewayException(x, format, args);
        }
    }

}
