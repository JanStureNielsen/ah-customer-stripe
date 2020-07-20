from stripe_rest import customer_api, product_api, price_api
from stripe_rest import payment_method_api, subscription_schedule_api

# ##############################################################################
# Create a Customer
# Create the Customer's Default Payment Method
# Create a Product add a Price
# Create a SubscriptionSchedule, attached to Customer
# ##############################################################################

# ##########################################################
# Create Customer
# Get Same Customer
# List all customers .. find our customer
#
email = "fredissuper@duper.com"
customer = customer_api.create_customer('''
{ 
    "name":"Fred Subscription", 
    "description":"Test Fred Subscription Test", 
    "email": "''' + email + '''"
}
''')
assert customer is not None, "Create Customer returned None for Customer."

customer_cid = customer['id']
assert customer_cid.startswith("cus"), "Create Customer returned unexpected customer id {}".format(customer_cid)

# ##########################################################
# Create PaymentMethod - Attach to customer
#
paymentMethod = payment_method_api.create_payment_method('''
{ "object": "payment_method", 
  "type":"card",
  "card": { 
      "exp_month": "8", 
      "exp_year": "2022", 
      "number": "4242424242424242",
      "cvc": "123"
    },
   "billing_details": {
    "address": {
      "city": "Palo Alto",
      "country": "US",
      "line1": "123 Main St",
      "postal_code": "94306",
      "state": "CA"
    },
    "email": "''' + email + '''",
    "name": "Fred Subscription",
    "phone": "435-555-1234"
  } 
}
''')
assert paymentMethod is not None, "Create PaymentMethod returned None."

payment_method_cid = paymentMethod['id']
assert payment_method_cid.startswith("pm_"), "Create PaymentMethod returned unexpected paymentMethod id {}".format(
    payment_method_cid)

# Attach to customer
json_attach_params = '{ "customer": "' + customer_cid + '" }'
attached_payment_method = payment_method_api.attach_payment_method_to_customer(payment_method_cid, json_attach_params)
attached_payment_method_cid = attached_payment_method['id']
assert payment_method_cid == attached_payment_method_cid, "PaymentMethod Id does not match attached : {} / {}".format(
    payment_method_cid, attached_payment_method_cid)

# ##########################################################
# *** Important ***
#   Set the Customer default Payment Method to the newly
#       created Payment Method
#
print("====================")
print("Setting default Payment Method '{}' on Customer '{}'".format(payment_method_cid, customer_cid))
print("=== ")
update_json = "{ 'invoice_settings': { 'default_payment_method': '" + payment_method_cid + "' } }"
customer = customer_api.update_customer(customer_cid, update_json)
assert customer is not None, "Update Customer Default payment Method returned None."

customer_cid_2 = customer['id']
assert customer_cid == customer_cid_2, "Customer Ids do not match for Update : {} / {}" \
    .format(customer_cid, customer_cid_2)

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

product_cid = product['id']
assert product_cid.startswith("prod"), "Create Product returned unexpected product id {}".format(product_cid)

# ##########################################################
# Create Price on Product
#
price = price_api.create_price('''
{ "product": "''' + product_cid + '''", 
  "currency":"usd", 
  "recurring": { "interval": "month" }, 
  "unit_amount": 1800 
}
''')
assert price is not None, "Create Price returned None for Price"

price_cid = price['id']
assert price_cid.startswith("price"), "Create Price returned unexpected price id {}".format(price_cid)

# ##########################################################
# Create Subscription_Schedule with attachment to customer
#
subscription_schedule = subscription_schedule_api.create_subscription_schedule('''
{ "collection_method":"charge_automatically", 
  "phases": [ {
      "plans": [ { 
          "price": "''' + price_cid + '''",
          "quantity": 1
        }
      ],
      "iterations": 12
    }          
  ],    
  "customer": "''' + customer_cid + '''",
  "start_date": "now",
  "end_behavior": "release"
}
''')

assert subscription_schedule is not None, "Create Subscription Schedule returned None for Subscription Schedule"

subscription_schedule_cid = subscription_schedule['id']
assert subscription_schedule_cid.startswith(
    "sub_sched_"), "Create Subscription Schedule returned unexpected subscription_schedule id {}".format(
    subscription_schedule_cid)

