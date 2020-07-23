package ah.helper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Map;

public class StripeHelper {
    private StripeHelper() {
    }

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

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

}
