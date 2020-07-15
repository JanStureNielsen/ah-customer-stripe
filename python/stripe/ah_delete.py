from stripe_rest import customer_api, product_api, price_api, subscription_api, subscription_item_api, subscription_schedule_api
from stripe_rest import payment_card_api


customers = customer_api.list_customers()
for customer in customers:
    payment_card_api.list_payment_cards_and_delete(customer['id'])

subscriptions = subscription_api.list_subscriptions()

for subscription in subscriptions:
    subscription_schedule_api.list_subscription_schedules_and_delete(subscription['id'])

for subscription in subscriptions:
    subscription_item_api.list_subscription_items_and_delete(subscription['id'])

subscription_api.list_subscriptions_and_delete()
# price_api.list_prices_and_delete()
product_api.list_products_and_delete()
customer_api.list_customers_and_delete()

# deleteCustomer()


