from stripe_rest import customer_api, product_api, price_api, subscription_api, subscription_item_api, subscription_schedule_api
from stripe_rest import payment_method_api, payment_card_api
import json
import sys

# ##############################################################################
# Create a Customer
# Create a Product
# Create a Price, add it to the product
# Create a SubscriptionItem
# Create a SubscriptionSchedule
# ##############################################################################

# ##########################################################
# Create Customer
# Get Same Customer
# List all customers .. find our customer
#
customer = customer_api.create_customer("{ 'name':'Fred', 'description':'Test Test'}")
assert customer is not None, "Create Customer returned None for Customer."

cust_cid = customer['id']
assert cust_cid.startswith("cus"), "Create Customer returned unexpected customer id {}".format(cust_cid)

print("Created Customer Id : " + str(cust_cid))
customer = customer_api.get_customer(cust_cid)
cust_cid_2 =  customer['id']
assert cust_cid == cust_cid_2, "Customer Ids do not match : {} / {}".format(cust_cid, cust_cid_2)

customers = customer_api.list_customers()
found = False
for cust_dict in customers:
    cid = cust_dict['id']
    if cust_cid == cid:
        found = True
        break

assert found, "Could not find matching customer for customer id {}".format(cust_cid)


# ##########################################################
# Create PaymentCard
# Get same PaymentCard
# List all PaymentCards .. find our paymentCard
#
paymentCard = payment_card_api.create_payment_card(cust_cid, '''
{ "object": "card", 
  "brand": "Visa", 
  "source": "tok_visa", 
  "country": "US", 
  "customer":"''' + cust_cid + '''", 
  "exp_month": 8, 
  "exp_year": 2022, 
  "funding":"credit", 
  "last4": "4242" 
}
''')
assert paymentCard is not None, "Create PaymentCard returned None for Price."

card_cid = paymentCard['id']
assert card_cid.startswith("card_"), "Create PaymentCard returned unexpected paymentCard id {}".format(card_cid)

payment_card_2 = payment_card_api.get_payment_card(cust_cid, card_cid)
print("PaymentCard Id : " + str(payment_card_2['id']))
card_cid_2 =  payment_card_2['id']
assert card_cid == card_cid_2, "PaymentCard Ids do not match : {} / {}".format(card_cid, card_cid_2)

paymentCards = payment_card_api.list_payment_cards(cust_cid)
found = False
for paymentCard3 in paymentCards:
    cid = paymentCard3['id']
    if card_cid == cid:
        found = True
        break

assert found, "Could not find matching paymentCard for paymentCard id {}".format(card_cid)



sys.exit(0)





# ##########################################################
# Create Product
# Get same Product
# List all Products .. find our product
#
product = product_api.create_product('''
{ "name": "Test (1) Product Name", 
  "description": "Test Product Description", 
  "statementDescriptor":"Test Product Stmt Desc", 
  "type":"service" 
}
''')
assert product is not None, "Create Product returned None for Price."

prod_cid = product['id']
assert prod_cid.startswith("prod"), "Create Product returned unexpected product id {}".format(prod_cid)

product2 = product_api.get_product(prod_cid)
print("Product Id : " + str(product2['id']))
prod_cid_2 =  product2['id']
assert prod_cid == prod_cid_2, "Product Ids do not match : {} / {}".format(prod_cid, prod_cid_2)

products = product_api.list_products()
found = False
for product3 in products:
    cid = product3['id']
    if prod_cid == cid:
        found = True
        break

assert found, "Could not find matching product for product id {}".format(prod_cid)

# ##########################################################
# Create Price on Product
# Get our Price
# List all prices and find our price
#
price = price_api.create_price('''
{ "product": "''' + prod_cid + '''", 
  "currency":"usd", 
  "recurring": { "interval": "month" }, 
  "unit_amount": 1800 
}
''')
assert price is not None, "Create Price returned None for Price"

price_cid = price['id']
assert price_cid.startswith("price"), "Create Price returned unexpected price id {}".format(price_cid)

price3 = price_api.get_price(price_cid)
print("Product Id : " + str(price3['id']))
price_cid_2 =  price3['id']
assert price_cid == price_cid_2, "Price Ids do not match : {} / {}".format(price_cid, price_cid_2)

prices = price_api.list_prices()
found = False
for price3 in prices:
    print("======================================== : Price")
    print(str(price3))
    print("========================================")
    cid = price3['id']
    if price_cid == cid:
        found = True
        break

assert found, "Could not find matching price for price id {}".format(price_cid)

# ##########################################################
# Create Subscriptions
# Read same Subscriptions
# List all subscriptions and find ours
#
json = '''
{ "customer": "''' + cust_cid + '''",
  "items": [ { "price": "''' + price_cid + '''" } ]
}
'''
subscription = subscription_api.create_subscription(json)
assert subscription is not None, "Create Subscription returned None for Subscription"

subscription_cid = subscription['id']
assert subscription_cid.startswith("subscription"), "Create Subscription returned unexpected subscription id {}".format(subscription_cid)

subscription3 = subscription_api.get_subscription(subscription_cid)
print("Product Id : " + str(subscription3['id']))
subscription_cid_2 =  subscription3['id']
assert subscription_cid == subscription_cid_2, "Subscription Ids do not match : {} / {}".format(subscription_cid, subscription_cid_2)

subscriptions = subscription_api.list_subscriptions()
found = False
for subscription3 in subscriptions:
    print("======================================== : Subscription")
    print(str(subscription3))
    print("========================================")
    cid = subscription3['id']
    if subscription_cid == cid:
        found = True
        break

assert found, "Could not find matching subscription for subscription id {}".format(subscription_cid)

# ##########################################################
# Create Subscription_Items
# Read same Subscription_Items
# List all Subscription Items and find ours
#
subscription_item = subscription_item_api.create_subscription_item('''
{ "xxxxxxxxxxx": "''' + prod_cid + '''",
  "collection_method":"charge_automatically",
  "customer": "''' + cust_cid + '''",
}
''')
assert subscription_item is not None, "Create Subscription Item returned None for Subscription Item"

subscription_item_cid = subscription_item['id']
assert subscription_item_cid.startswith("subscription_item"), "Create Subscription Item returned unexpected subscription_item id {}".format(subscription_item_cid)

subscription_item_2 = subscription_item_api.get_subscription_item(subscription_item_cid)
subscription_item_cid_2 =  subscription_item_2['id']
assert subscription_item_cid == subscription_item_cid_2, "Customer Ids do not match : {} / {}".format(subscription_item_cid, subscription_item_cid_2)

subscription_items = subscription_item_api.list_subscription_items()
found = False
for subscription_item_3 in subscription_items:
    print("======================================== : Price")
    print(str(subscription_item_3))
    print("========================================")
    cid = subscription_item_3['id']
    if subscription_item_cid == cid:
        found = True
        break

assert found, "Could not find matching subscription_item for subscription_item id {}".format(subscription_item_cid)

# ##########################################################
# Create Subscription_Schedules
# Read same Subscription_Schedules
# List all Subscription Schedules and find ours
#
subscription_schedule = subscription_schedule_api.create_subscription_schedule('''
{ "xxxxxxxxxxx": "''' + prod_cid + '''",
  "collection_method":"charge_automatically",
  "customer": "''' + cust_cid + '''",
}
''')

assert subscription_schedule is not None, "Create Subscription Schedule returned None for Subscription Schedule"

subscription_schedule_cid = subscription_schedule['id']
assert subscription_schedule_cid.startswith("subscription_schedule"), "Create Subscription Schedule returned unexpected subscription_schedule id {}".format(subscription_schedule_cid)

subscription_schedule_2 = subscription_schedule_api.get_subscription_schedule(subscription_schedule_cid)
subscription_schedule_cid_2 =  subscription_schedule_2['id']
assert subscription_schedule_cid == subscription_schedule_cid_2, "Customer Ids do not match : {} / {}".format(subscription_schedule_cid, subscription_schedule_cid_2)

subscription_schedules = subscription_schedule_api.list_subscription_schedules()
found = False
for subscription_schedule_3 in subscription_schedules:
    print("======================================== : Price")
    print(str(subscription_schedule_3))
    print("========================================")
    cid = subscription_schedule_3['id']
    if subscription_schedule_cid == cid:
        found = True
        break

assert found, "Could not find matching subscription_schedule for subscription_schedule id {}".format(subscription_schedule_cid)

print ("=======================================")
print ("=======================================")
print ("=======================================")

# print ("custCid : " + str(custCid))

# price.list_prices_and_delete()
# product.list_products_and_delete()
# customer.list_customers_and_delete()

# deleteCustomer()


