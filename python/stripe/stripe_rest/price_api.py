import stripe
import requests
import json
from stripe_rest import ah_debug, ah_constant

baseurl = ah_constant.baseurl()

def create_price(params):
    response = requests.post(baseurl + "/price", params)
    print("Create Product : " + str(response))
    price_response = json.loads(response.content)

    if price_response['status'] != 200:
        ah_debug.raise_error(price_response, "Create Price : Returned Bad Http Status")
    return price_response['entity']

def get_price(price_cid):
    response = requests.get(baseurl + "/price/{}".format(price_cid))
    print("Get Product : " + str(response))
    price_response = json.loads(response.content)

    if price_response['status'] != 200:
        ah_debug.raise_error(price_response, "Get Price : Returned Bad Http Status")
    return price_response['entity']

def delete_price(price_cid):
    response = requests.delete(baseurl + "/price/{}".format(price_cid))
    print("Delete Price : " + str(response))
    delete_response = json.loads(response.content)

    if delete_response['status'] != 200:
        ah_debug.raise_error(delete_response, "Delete Price : Returned Bad Http Status")
    return delete_response['entity']

def list_prices():
    list_dict = """{"limit": 999999, "active": True}"""
    response = requests.request(method = "get", url = baseurl + "/prices", data = list_dict)
    print("List Price : " + str(response))
    prices_response = json.loads(response.content)

    if prices_response['status'] != 200:
        ah_debug.raise_error(prices_response, "List Prices : Returned Bad Http Status")
    return prices_response['entities']

def list_prices_and_delete():
    prices = list_prices()
    i = 0
    for price in prices:
        i += 1
        print("--------------------- " + str(i))
        if 'unitAmount' in price:
            if price['unitAmount'] is not None and 2000 > price['unitAmount']:
                print("Deleting Price Id : " + price['id'])
                delete_price(price['id'])

