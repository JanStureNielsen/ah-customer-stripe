import stripe
import requests
import json
from stripe_rest import ah_debug, ah_constant

baseurl = ah_constant.baseurl()

def create_subscription_item(params):
    response = requests.post(baseurl + "/subscription_item", params)
    print("Create SubscriptionItem : " + str(response))

    subscription_item_response = json.loads(response.content)
    if subscription_item_response['status'] != 200:
        ah_debug.raise_error(subscription_item_response, "Create Subscription Item : Returned Bad Http Status")
    return subscription_item_response['entity']

def get_subscription_item(subscription_item_cid):
    response = requests.get(baseurl + "/subscriptionItem/{}".format(subscription_item_cid))
    print("Get SubscriptionItem : " + str(response))
    subscription_item_response = json.loads(response.content)

    if subscription_item_response['status'] != 200:
        ah_debug.raise_error(subscription_item_response, "Get Subscription Item : Returned Bad Http Status")
    return subscription_item_response['entity']

def delete_subscription_item(subscription_item_cid):
    response = requests.delete(baseurl + "/subscriptionItem/{}".format(subscription_item_cid))
    print("Delete SubscriptionItem : " + str(response))

    response = requests.delete(baseurl + "/subscription_item/{}".format(subscription_item_cid))
    print("Delete SubscriptionItem : " + str(response))
    delete_response = json.loads(response.content)

    if delete_response['status'] != 200:
        ah_debug.raise_error(delete_response, "Delete Subscription Item : Returned Bad Http Status")
    return delete_response['entity']

def list_subscription_items(subscription_cid):
    response = requests.get(baseurl + "/subscriptionItems/all/{}".format(subscription_cid))
    print("List SubscriptionItem : " + str(response))
    subscription_items_response = json.loads(response.content)

    if subscription_items_response['status'] != 200:
        ah_debug.raise_error(subscription_items_response, "List Subscription Item : Returned Bad Http Status")
    return subscription_items_response['entities']

def list_subscription_items_and_delete(subscription_cid):
    subscription_items = list_subscription_items(subscription_cid)
    i = 0
    for subscription_item in subscription_items:
        i += 1
        print("--------------------- " + str(i))
        print("Deleting SubscriptionItem Id : " + subscription_item['id'])
        delete_subscription_item(subscription_item['id'])
