# Ad Hoc Markets 

[[_TOC_]]

### Overview
The input for all the REST calls in the Stripe Customer Object in JSON form.  As described at https://stripe.com/docs/api/customers

### Stack
<<<<<<< HEAD
* Java 11
=======
* Spring Boot REST
* Spring Boot (see src/main/java/ah/customer/stripe/Application.java)
* Spring Rest (see src/main/java/ah/customer/stripe/controller/StripeController.java)
* Java 11
* Spring Boot/Rest
* Spring Boot (see src/main/java/ah/Application.java)
* Spring Rest (see src/main/java/ah/customer/stripe/controller/*Controller.java
>>>>>>> 8aaaed6d5691b12d11809d9bdc19acb003f1319a
* Lombok
* Spring Boot (see src/main/java/ah/customer/stripe/Application.java)
* Spring Boot REST
* Spring Rest (see src/main/java/ah/customer/stripe/controller/StripeController.java)

### Build & Run Jar

mvn clean package spring-boot:repackage

java -jar ./target/adhocmarkets-stripe-0.0.1-SNAPSHOT.jar

### Python 3 scripts (.../python/stripe/*)
| Script | Description |
| ------- | ----------- |
| ah_make_subscription.py | Small sample making a Customer/Product/Price/Subscription| 
| ah_delete.py : | Example script removing test data allowed by Stripe |          
| ah_stripe_tests.py | Example wee integration test |      

### Misc.

<<<<<<< HEAD
* I tried adding swagger; however, the Stripe Customer object, I'm guessing, has recursion which 
caused the swagger page to hang. I lost a few hours on this.
* I didn't add Spring Data or Caching because there are data elements yet.
* The 'Update' for Customer is weird.  I don't understand it, it seems the cached data is stored under
the 'metadata' attribute. I lost a few hours attempting to understand this.

### Misc.
=======
>>>>>>> 8aaaed6d5691b12d11809d9bdc19acb003f1319a
 
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
