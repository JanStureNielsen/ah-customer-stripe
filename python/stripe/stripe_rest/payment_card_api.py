import stripe
import requests
import json
from stripe_rest import ah_debug, ah_constant

baseurl = ah_constant.baseurl()

def create_payment_card(customer_cid, params):
    url = baseurl + "/paymentCard/{}".format(customer_cid)
    response = requests.post(url, params)
    print("Create Payment Card : " + str(response))

    card_response = json.loads(response.content)
    if card_response['status'] != 200:
        ah_debug.raise_error(card_response, "Create Payment Card  : Returned Bad Http Status")
    return card_response['entity']

def get_payment_card(customer_cid, card_cid):
    response = requests.get(baseurl + "/paymentCard/{}/{}".format(customer_cid, card_cid))
    print("Get Payment Card : " + str(response))
    card_response = json.loads(response.content)

    if card_response['status'] != 200:
        ah_debug.raise_error(card_response, "Get Payment Card  : Returned Bad Http Status")
    return card_response['entity']

def delete_payment_card(card_cid):
    response = requests.delete(baseurl + "/paymentCard/{}".format(card_cid))
    print("Delete Payment Card : " + str(response))

    response = requests.delete(baseurl + "/paymentCard/{}".format(card_cid))
    print("Delete Payment Card : " + str(response))
    delete_response = json.loads(response.content)

    if delete_response['status'] != 200:
        ah_debug.raise_error(delete_response, "Delete Payment Card  : Returned Bad Http Status")
    return delete_response['entity']

def list_payment_cards(customer_cid):
    response = requests.get(baseurl + "/paymentCards/all/{}".format(customer_cid), "{}")
    print("List Payment Card : " + str(response))
    cards_response = json.loads(response.content)

    if cards_response['status'] != 200:
        ah_debug.raise_error(cards_response, "List Payment Card  : Returned Bad Http Status")
    return cards_response['entities']

def list_payment_cards_and_delete(cust_cid):
    cards = list_payment_cards(cust_cid)
    i = 0
    for card in cards:
        i += 1
        print("--------------------- " + str(i))
        print("Deleting Payment Card Id : " + card['id'])
        delete_payment_card(card['id'])
