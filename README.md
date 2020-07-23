# Ad Hoc Markets 

### Overview
The input for all the REST calls in the [Stripe Customer Object in JSON form](https://stripe.com/docs/api/customers).

### Stack
* Java 11
* Lombok
* Spring Boot (see src/main/java/ah/customer/stripe/Application.java)
* Spring REST (see [src/main/java/ah/customer/stripe/controller/StripeController.java]())
* Spring Boot (see src/main/java/ah/customer/stripe/Application.java)

### Building & Running

    mvn clean package spring-boot:repackage

    java -jar ./target/*.jar

### Python 3 scripts (.../python/stripe/*)

| Script | Description |
| ------- | ----------- |
| [ah_make_subscription.py](tree/master/python/stripe/ah_make_subscription.py) | Small sample making a Customer/Product/Price/Subscription|
| [ah_delete.py](tree/master/python/stripe/ah_delete.py) | Example script removing test data allowed by Stripe |
| [ah_stripe_tests.py](tree/master/python/stripe/ah_stripe_tests.py) | Example wee integration test |

### Misc.

* I tried adding swagger; however, the Stripe Customer object, I'm guessing, has recursion which 
caused the Swagger page to hang. I lost a few hours on this.
* I didn't add Spring Data or Caching because there are data elements yet.
* No API to remove a Price, but you can manually on the Stripe admin page; you can archive a Price.
* No API to Subscription, Subscription Schedule; the API allows you to cancel them.
 
#### Get Customer
Requires authentication.

```
curl http://localhost:8080/api/customer/<ID> -X GET 
```

#### Create Customer
Starts as anonymous, then creates a customer with a password.

```
curl http://localhost:8080/api/customer \
  -X POST \
  -H "Content-Type: application/json" \
  -d '{ "description": "My Forth Test Customer" }'   
```

#### Update Customer
Requires authentication. This should be a non-destructive update.

```
curl http://localhost:8080/api/customer \
  -X PUT \
  -H "Content-Type: application/json" \
  -d '{ "id": "'${CID}'", "description": "(2)My Updated Forth Test Customer", "email" : "bob.bigboy_002@food.me" }'   
```

#### Delete Customer
Requires admin authentication.

```
curl http://localhost:8080/api/customer/<ID> -X DELETE 
```
