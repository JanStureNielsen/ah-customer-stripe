import stripe
import requests
import json
from stripe_rest import ah_debug, ah_constant

baseurl = ah_constant.baseurl()

def create_payment_method(params):
    response = requests.post(baseurl + "/paymentMethod", params)
    print("Create Payment Method : " + str(response))
    payment_method_response = json.loads(response.content)

    if payment_method_response['status'] != 200:
        ah_debug.raise_error(payment_method_response, "Create Payment Method : Returned Bad Http Status")
    return payment_method_response['entity']

def get_payment_method(payment_method_cid):
    response = requests.get(baseurl + "/paymentMethod/{}".format(payment_method_cid))
    print("Get Payment Method : " + str(response))
    payment_method_response = json.loads(response.content)

    if payment_method_response['status'] != 200:
        ah_debug.raise_error(payment_method_response, "Get Payment Method : Returned Bad Http Status")
    return payment_method_response['entity']

def detach_payment_method_from_customer(payment_method_cid):
    response = requests.delete(baseurl + "/paymentMethod/detach/{}".format(payment_method_cid))
    print("Detach Payment Method : " + str(response))

    delete_response = json.loads(response.content)
    if delete_response['status'] != 200:
        ah_debug.raise_error(delete_response, "Detach Payment Method : Returned Bad Http Status")
    return delete_response['entity']

def attach_payment_method_to_customer(payment_method_cid, params):
    response = requests.post(baseurl + "/paymentMethod/attach/{}".format(payment_method_cid), params)
    print("Attach Payment Method : " + str(response))

    attach_response = json.loads(response.content)
    if attach_response['status'] != 200:
        ah_debug.raise_error(attach_response, "Attach Payment Method : Returned Bad Http Status")
    return attach_response['entity']

def list_payment_methods(params):
    response = requests.request(url = baseurl + "/paymentMethods", method = 'get', data = params)
    print("List Payment Methods : " + str(response))

    list_response = json.loads(response.content)
    if list_response['status'] != 200:
        ah_debug.raise_error(list_response, "List Payment Methods : Returned Bad Http Status")
    return list_response['entities']

