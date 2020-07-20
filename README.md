# Ad Hoc Markets 

[[_TOC_]]

### Overview
The input for all the REST calls in the Stripe Customer Object in json form.  As described at https://stripe.com/docs/api/customers

### Stack
* Java 11
* Spring Boot/Rest
* Spring Boot (see src/main/java/ah/Application.java)
* Spring Rest (see src/main/java/ah/customer/stripe/controller/*Controller.java
* Lombok

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

* I tried adding Swagger and OpenAPI; however, the Stripe Customer object, I'm guessing, has recurstion which 
caused the output pages to hang. I lost a few hours on this.
* I didn't add Spring Data or Caching because there are no data elements yet.

### Misc.
 
* No API to remove a Price, but you can manually on the Sripe admin page.
    * You can archive a Price.
* No API to Subscription, Subscription Schedule. 
    * The API allows you to Cancel them
     