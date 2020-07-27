package ah.customer.stripe;

import ah.helper.StripeHelper;
import com.stripe.net.ApiRequestParams;
import com.stripe.param.*;

public enum StripeParam {

    BANK_ACCOUNT_CREATE(PaymentSourceCollectionCreateParams.class),
    BANK_ACCOUNT_LIST(PaymentSourceCollectionListParams.class),
    BANK_ACCOUNT_UPDATE(BankAccountUpdateOnAccountParams.class),
    CUSTOMER_CREATE(CustomerCreateParams.class),
    CUSTOMER_LIST(CustomerListParams.class),
    CUSTOMER_UPDATE(CustomerUpdateParams.class),
    PAYMENT_CARD_CREATE(PaymentSourceCollectionCreateParams.class),
    PAYMENT_CARD_LIST(PaymentSourceCollectionListParams.class),
    PAYMENT_CARD_UPDATE(CardUpdateOnAccountParams.class),
    PAYMENT_METHOD_ATTACH(PaymentMethodAttachParams.class),
    PAYMENT_METHOD_CREATE(PaymentMethodCreateParams.class),
    PAYMENT_METHOD_LIST(PaymentMethodListParams.class),
    PAYMENT_METHOD_UPDATE(PaymentMethodUpdateParams.class),
    PAYMENT_SOURCE_CREATE(SourceCreateParams.class),
    PAYMENT_SOURCE_LIST(PaymentSourceCollectionListParams.class),
    PAYMENT_SOURCE_UPDATE(SourceUpdateParams.class),
    PRICE_CREATE(PriceCreateParams.class),
    PRICE_LIST(PriceListParams.class),
    PRICE_UPDATE(PriceUpdateParams.class),
    PRODUCT_LIST(ProductListParams.class),
    PRODUCT_CREATE(ProductCreateParams.class),
    PRODUCT_UPDATE(ProductUpdateParams.class),
    SUBSCRIPTION_CREATE(SubscriptionCreateParams.class),
    SUBSCRIPTION_LIST(SubscriptionListParams.class),
    SUBSCRIPTION_UPDATE(SubscriptionUpdateParams.class),
    SUBSCRIPTION_ITEM_CREATE(SubscriptionItemCreateParams.class),
    SUBSCRIPTION_ITEM_LIST(SubscriptionItemListParams.class),
    SUBSCRIPTION_ITEM_UPDATE(SubscriptionItemUpdateParams.class),
    SUBSCRIPTION_SCHEDULE_CREATE(SubscriptionScheduleCreateParams.class),
    SUBSCRIPTION_SCHEDULE_LIST(SubscriptionScheduleListParams.class),
    SUBSCRIPTION_SCHEDULE_UPDATE(SubscriptionScheduleUpdateParams.class);

    final Class<? extends ApiRequestParams> clazz;

    StripeParam(Class<? extends ApiRequestParams> clazz) {
        this.clazz = clazz;
    }

    @SuppressWarnings("unchecked")
    public <T extends ApiRequestParams> T fromJson(String json) {
        return (T) StripeHelper.getGson().fromJson(json, clazz);
    }
}
