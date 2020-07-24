import stripe
import requests
import json
from stripe_rest import ah_debug, ah_constant

baseurl = ah_constant.baseurl()

def create_customer(params):
    response = requests.post(baseurl + "/customers", params)
    print("Create Customer : " + str(response))
    customer_response = json.loads(response.content)

    if customer_response['status'] != 200:
        ah_debug.raise_error(customer_response, "Create Customer : Returned Bad Http Status")
    return customer_response['entity']

def get_customer(custId):
    response = requests.get(baseurl + "/customers/{}".format(custId))
    print("Get Customer : " + str(response))
    customer_response = json.loads(response.content)

    if customer_response['status'] != 200:
        ah_debug.raise_error(customer_response, "Get Customer : Returned Bad Http Status")
    return customer_response['entity']

def update_customer(cust_cid, params):
    response = requests.request(method = 'put', url = baseurl + "/customers/{}".format(cust_cid), data = params)
    print("Update Customer : " + str(response))

    update_response = json.loads(response.content)
    if update_response['status'] != 200:
        ah_debug.raise_error(update_response, "Update Customer : Returned Bad Http Status")
    return update_response['entity']

def delete_customer(cust_cid):
    response = requests.delete(baseurl + "/customers/{}".format(cust_cid))
    print("Delete Customer : " + str(response))

    delete_response = json.loads(response.content)
    if delete_response['status'] != 200:
        ah_debug.raise_error(delete_response, "Delete Customer : Returned Bad Http Status")
    return delete_response['entity']

def list_customers():
    response = requests.get(baseurl + "/customers/all")
    print("List Customers : " + str(response))

    list_response = json.loads(response.content)
    if list_response['status'] != 200:
        ah_debug.raise_error(list_response, "List Customers : Returned Bad Http Status")
    return list_response['entities']

def list_customers_and_delete():
    customers = list_customers()

    i = 0
    for customer in customers:
        i += 1
        print("--------------------- " + str(i))
        if 'description' in customer:
            if customer['description'] is not None and 'Test' in customer['description']:
                print("Deleting Customer Id : " + customer['id'])
                delete_customer(customer['id'])

# listCustomers()
# deleteCustomer()
