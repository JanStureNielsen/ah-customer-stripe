import stripe
import requests
import json
from stripe_rest import ah_debug

baseurl = "http://localhost:8080/api/v1"

def create_payment_method(params):
    response = requests.post(baseurl + "/paymentMethod", params)
    print("Create Payment Method : " + str(response))
    payment_method_response = json.loads(response.content)

    if payment_method_response['status'] != 200:
        ah_debug.raise_error(payment_method_response, "Create Payment Method return bad Http Status")
    return payment_method_response['entity']

def get_payment_method(custId):
    response = requests.get(baseurl + "/paymentMethod/{}".format(custId))
    print("Get Payment Method : " + str(response))
    payment_method_response = json.loads(response.content)

    if payment_method_response['status'] != 200:
        ah_debug.raise_error(payment_method_response, "Get Payment Method return bad Http Status")
    return payment_method_response['entity']

def detach_payment_method_from_customer(payment_methodCid):
    response = requests.delete(baseurl + "/paymentMethod/{}".format(payment_methodCid))
    print("Delete Payment Method : " + str(response))

    delete_response = json.loads(response.content)
    if delete_response['status'] != 200:
        ah_debug.raise_error(delete_response, "Delete Payment Method return bad Http Status")
    return delete_response['entity']

def attach_payment_method_to_customer(payment_methodCid):
    response = requests.delete(baseurl + "/paymentMethod/{}".format(payment_methodCid))
    print("Delete Payment Method : " + str(response))

    delete_response = json.loads(response.content)
    if delete_response['status'] != 200:
        ah_debug.raise_error(delete_response, "Delete Payment Method return bad Http Status")
    return delete_response['entity']

def list_payment_methods():
    response = requests.get(baseurl + "/paymentMethods/all")
    print("List Payment Methods : " + str(response))

    list_response = json.loads(response.content)
    if list_response['status'] != 200:
        ah_debug.raise_error(list_response, "List Payment Methods return bad Http Status")
    return list_response['entities']

def list_payment_methods_and_delete():
    payment_methods = list_payment_methods()

    i = 0
    for payment_method in payment_methods:
        i += 1
        print("--------------------- " + str(i))
        if 'description' in payment_method:
            if payment_method['description'] is not None and 'Test' in payment_method['description']:
                print("Deleting Payment Method Id : " + payment_method['id'])
                delete_payment_method(payment_method['id'])

