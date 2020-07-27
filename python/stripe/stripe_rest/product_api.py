import json

import requests
from stripe_rest import ah_debug, ah_constant

baseurl = ah_constant.baseurl()


def create_product(params):
    response = requests.post(baseurl + "/products/", params)
    print("Create Product : " + str(response))
    product_response = json.loads(response.content)

    if product_response['status'] != 200:
        ah_debug.raise_error(product_response, "Create Product : Returned Bad Http Status")
    return product_response['entity']


def get_product(product_cid):
    response = requests.get(baseurl + "/products/{}".format(product_cid))
    print("Get Product : " + str(response))
    product_response = json.loads(response.content)

    if product_response['status'] != 200:
        ah_debug.raise_error(product_response, "Get Product : Returned Bad Http Status")
    return product_response['entity']


def delete_product(product_cid):
    response = requests.delete(baseurl + "/products/{}".format(product_cid))
    print("Delete Product : " + str(response))

    delete_response = json.loads(response.content)
    # 409 == CONFLICT: Product can't be deleted because it has an attached Price.
    if delete_response['status'] not in ( 200, 404 ):
        ah_debug.raise_error(delete_response, "Delete Product : Returned Bad Http Status")
    if delete_response['status'] == 404:
        return {}
    return delete_response['entity']


def list_products():
    list_dict = """{"limit": 999999, "active": True}"""
    response = requests.request(method="get", url=baseurl + "/products/", data=list_dict)
    print("List Product : " + str(response))
    products_response = json.loads(response.content)

    if products_response['status'] != 200:
        raise Exception("List Products : Returned Bad Http Status of '{}'".format(products_response['status']))
    return products_response['entities']

def list_products_and_delete():
    products = list_products()
    i = 0
    for product in products:
        i += 1
        print("--------------------- " + str(i))
        if 'description' in product:
            if product['description'] is not None and 'Test' in product['description']:
                print("Deleting Product Id : " + product['id'])
                delete_product(product['id'])
