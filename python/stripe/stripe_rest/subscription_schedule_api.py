import stripe
import requests
import json
from stripe_rest import ah_debug, ah_constant

baseurl = ah_constant.baseurl()

def create_subscription_schedule(params):
    response = requests.post(baseurl + "/subscription_schedule", params)
    print("Create SubscriptionSchedule : " + str(response))

    subscription_schedule_response = json.loads(response.content)
    if subscription_schedule_response['status'] != 200:
        ah_debug.raise_error(subscription_schedule_response, "Create Subscription Schedule : Returned Bad Http Status")
    return subscription_schedule_response['entity']

def get_subscription_schedule(subscription_schedule_cid):
    response = requests.get(baseurl + "/subscriptionSchedule/{}".format(subscription_schedule_cid))
    print("Get SubscriptionSchedule : " + str(response))
    subscription_schedule_response = json.loads(response.content)

    if subscription_schedule_response['status'] != 200:
        ah_debug.raise_error(subscription_schedule_response, "Get Subscription Schedule : Returned Bad Http Status")
    return subscription_schedule_response['entity']

def delete_subscription_schedule(subscription_schedule_cid):
    response = requests.delete(baseurl + "/subscriptionSchedule/{}".format(subscription_schedule_cid))
    print("Delete SubscriptionSchedule : " + str(response))

    response = requests.delete(baseurl + "/subscriptionSchedule/{}".format(subscription_schedule_cid))
    print("Delete SubscriptionSchedule : " + str(response))
    delete_response = json.loads(response.content)

    if delete_response['status'] != 200:
        ah_debug.raise_error(delete_response, "Delete Subscription Schedule : Returned Bad Http Status")
    return delete_response['entity']

def list_subscription_schedules(subscription_cid):
    response = requests.get(baseurl + "/subscriptionSchedules/all/{}".format(subscription_cid))
    print("List SubscriptionSchedule : " + str(response))
    subscription_schedules_response = json.loads(response.content)

    if subscription_schedules_response['status'] != 200:
        ah_debug.raise_error(subscription_schedules_response, "List Subscription Schedule : Returned Bad Http Status")
    return subscription_schedules_response['entities']

def list_subscription_schedules_and_delete(subscription_cid):
    subscription_schedules = list_subscription_schedules(subscription_cid)
    i = 0
    for subscription_schedule in subscription_schedules:
        i += 1
        print("--------------------- " + str(i))
        print("Deleting SubscriptionSchedule Id : " + subscription_schedule['id'])
        delete_subscription_schedule(subscription_schedule['id'])
