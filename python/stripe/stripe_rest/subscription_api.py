import stripe
import requests
import json
from stripe_rest import ah_debug

baseurl = "http://localhost:8080/api/v1"

def create_subscription(params):
    response = requests.post(baseurl + "/subscription", params)
    print("Create Subscription : " + str(response))

    subscription_response = json.loads(response.content)
    if subscription_response['status'] != 200:
        ah_debug.raise_error(subscription_response, "Create Subscription return bad Http Status")
    return subscription_response['entity']

def get_subscription(subscription_cid):
    response = requests.get(baseurl + "/subscription/{}".format(subscription_cid))
    print("Get Subscription : " + str(response))
    subscription_response = json.loads(response.content)

    if subscription_response['status'] != 200:
        ah_debug.raise_error(subscription_response, "Get Subscription return bad Http Status")
    return subscription_response['entity']

def delete_subscription(subscription_cid):
    response = requests.delete(baseurl + "/subscription/{}".format(subscription_cid))
    print("Delete Subscription : " + str(response))

    response = requests.delete(baseurl + "/subscription/{}".format(subscription_cid))
    print("Delete Subscription : " + str(response))
    delete_response = json.loads(response.content)

    if delete_response['status'] != 200:
        ah_debug.raise_error(delete_response, "Delete Subscription return bad Http Status")
    return delete_response['entity']


def list_subscriptions():
    response = requests.get(baseurl + "/subscriptions/all")
    print("List Subscription : " + str(response))
    subscriptions_response = json.loads(response.content)

    if subscriptions_response['status'] != 200:
        ah_debug.raise_error(subscriptions_response, "List Subscription return bad Http Status")
    return subscriptions_response['entities']

def list_subscriptions_and_delete():
    subscriptions = list_subscriptions()
    i = 0
    for subscription in subscriptions:
        i += 1
        print("--------------------- " + str(i))
        print("Deleting Subscription Id : " + subscription['id'])
        delete_subscription(subscription['id'])
