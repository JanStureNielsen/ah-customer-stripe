# Ad Hoc Markets 

### Overview
The input for all the REST calls in the Stripe Customer Object in json form.  As described at https://stripe.com/docs/api/customers

### Stack
* Java 11
* Spring Boot/Rest
* Spring Boot (see src/main/java/com/fem/adhoc/StripeApplication.java)
* Spring Rest (see src/main/java/com/fem/adhoc/controller/StripeController.java)
* Lombok

### Build & Run Jar

mvn clean package spring-boot:repackage

java -jar ./target/adhocmarkets-stripe-0.0.1-SNAPSHOT.jar


### Misc.

* I tried adding swagger; however, the Stripe Customer object, I'm guessing, has recurstion which 
caused the swagger page to hang. I lost a few hours on this.
* I didn't add Spring Data or Caching because there are data elements yet.
* The 'Update' for Customer is weird.  I don't understand it, it seems the cahgned data is stored under
the 'metadata' attribute. I lost a few hours attempting to understand this.

### curl examples:
 
#### Get Customer

```
curl http://localhost:8080/api/v1/customer/<ID> -X GET 
```

#### Create Customer

```
curl http://localhost:8080/api/v1/customer \
  -X POST \
  -H "Content-Type: application/json" \
  -d '{ "description": "My Forth Test Customer" }'   
```

#### Update Customer

```
curl http://localhost:8080/api/v1/customer \
  -X PUT \
  -H "Content-Type: application/json" \
  -d '{ "id": "'${CID}'", "description": "(2)My Updated Forth Test Customer", "email" : "bob.bigboy_002@food.me" }'   
```

#### Delete Customer

```
curl http://localhost:8080/api/v1/customer/<ID> -X DELETE 
```
  