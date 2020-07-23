package ah.helper;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.CustomerCollection;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerListParams;
import com.stripe.param.CustomerUpdateParams;

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

    @SuppressWarnings("serial")
    public static Map<String, Object> inactive() {
        return new HashMap<String, Object>() {{
            put("active", false);
        }};
    }

    public static CustomerCollection customerCollection(String customerListParamString) {
        try {
            return customerCollection(customerListParams(customerListParamString));
        } catch (StripeException x) {
            throw new StripeGatewayException(x, "Customer list retrieve failed for '%s'.", customerListParamString);
        }
    }

    public static Customer customerCreate(String customerCreateParamString) {
        try {
            return Customer.create(customerCreateParams(customerCreateParamString));
        } catch (StripeException x) {
            throw new StripeGatewayException(x, "Customer create failed with parameter string '%s'.", customerCreateParamString);
        }
    }

    public static Customer customerRetrieve(String customerId) {
        try {
            return Customer.retrieve(customerId);
        } catch (StripeException x) {
            throw new StripeGatewayException(x, "Customer retrieve failed for '%s'.", customerId);
        }
    }

    public static Customer customerUpdate(String customerId, String customerUpdateParamString) {
        try {
            return customerRetrieve(customerId).update(customerUpdateParams(customerUpdateParamString));
        } catch (StripeException x) {
            throw new StripeGatewayException(x, "Cutomer update failed for '%s' with '%s'", customerId, customerUpdateParamString);
        }
    }

    public static Customer customerDelete(String customerId) {
        try {
            return customerRetrieve(customerId).delete();
        } catch (StripeException x) {
            throw new StripeGatewayException(x, "Cutomer delete failed for '%s'", customerId);
        }
    }

    private static CustomerCreateParams customerCreateParams(String customerCreateParamString) {
        return gson.fromJson(customerCreateParamString, CustomerCreateParams.class);
    }

    private static CustomerListParams customerListParams(String customerListParamString) {
        return gson.fromJson(customerListParamString, CustomerListParams.class);
    }

    private static CustomerUpdateParams customerUpdateParams(String customerUpdateParamString) {
        return gson.fromJson(customerUpdateParamString, CustomerUpdateParams.class);
    }

    private static CustomerCollection customerCollection(CustomerListParams customerListParams) throws StripeException {
        return Customer.list(customerListParams);
    }

}
