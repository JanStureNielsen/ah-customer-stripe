package ah.customer.stripe.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Map;

public class StripeHelper {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

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
}
