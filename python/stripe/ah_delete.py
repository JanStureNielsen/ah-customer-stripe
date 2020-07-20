from stripe_rest import customer_api, product_api, price_api, subscription_api, subscription_item_api, subscription_schedule_api
from stripe_rest import payment_card_api, payment_method_api

# Remove payment cards
customers = customer_api.list_customers()
for customer in customers:
    payment_card_api.list_payment_cards_and_delete(customer['id'])

    # Detach payment methods
    customer_cid = customer['id']
    pm_list_json = " { 'customer': '" + customer_cid + "', 'type': 'card', 'limit': 9999999 } "
    payment_methods = payment_method_api.list_payment_methods(pm_list_json)
    for payment_method in payment_methods:
        payment_method_api.detach_payment_method_from_customer(payment_method['id'])

    subscription_schedule_api.list_subscription_schedules_and_delete(customer_cid)


# Remove Subscription Schedules, Subscription Items, and Subscriptions
subscription_api.list_subscriptions_and_cancel()

subscriptions = subscription_api.list_subscriptions()
# for subscription in subscriptions:
#     subscription_schedule_api.list_subscription_schedules_and_delete(subscription['id'])

for subscription in subscriptions:
    subscription_item_api.list_subscription_items_and_delete(subscription['id'])

# price_api.list_prices_and_delete() # No APi to delete Prices - must be done manually
price_api.list_prices_and_delete()
product_api.list_products_and_delete()
customer_api.list_customers_and_delete()
