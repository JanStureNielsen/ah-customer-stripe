import stripe
import requests
import json

baseurl = "http://localhost:8080/api/v1"

def create_plan(params):
    response = requests.post(baseurl + "/plan", params)
    print("Create Plan : " + str(response))
    plan = json.loads(response.content)
    return plan['id']

def get_plan(prodId):
    response = requests.get(baseurl + "/plan/{}".format(prodId))
    print("Get Plan : " + str(response))
    plan = json.loads(response.content)
    return plan

def delete_plan(planCid):
    response = requests.delete(baseurl + "/plan/{}".format(planCid))
    print("Delete Plan : " + str(response))

def list_plans():
    response = requests.get(baseurl + "/plans/all")
    print("List Plan : " + str(response))
    plans = json.loads(response.content)
    return plans

def list_plans_and_delete():
    plans = list_plans()
    i = 0
    for plan in plans:
        i += 1
        print("--------------------- " + str(i))
        if 'amount' in plan:
            if plan['amount'] is not None and 2000 > plan['amount']:
                print("Deleting Plan Id : " + plan['id'])
                delete_plan(plan['id'])
