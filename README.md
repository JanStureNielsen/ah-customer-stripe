# Ad Hoc Markets 

### Overview
The input for all the REST calls in the [Stripe Customer Object in JSON form](https://stripe.com/docs/api/customers).

### Stack
* Java 8+ with Lombok
* Spring Boot (see [Application.java](src/main/java/ah/Application.java))
* Spring REST (see [Stripe controllers](src/main/java/ah/customer/stripe/controller/))

### Building & Running gateway

    # build executable JAR
    mvn package
    
    # run executable JAR
    java -jar ./target/*.jar

### Python 3 test scripts

    # create customer, product, price, subscription
    python3 python/stripe/ah_make_subscription.py
    
    # delete test data as allowed by Stripe
    python3 python/stripe/ah_delete.py
    
    # run Strip integration tests
    python3 python/stripe/ah_stripe_tests.py

### Misc.

* I tried adding Swagger; however, the Stripe Customer object, I'm guessing, has recursion which 
caused the Swagger page to hang. I lost a few hours on this.
* I didn't add Spring Data or Caching because there are data elements yet.
* No API to remove a Price, but you can manually on the Stripe admin page; you can archive a Price.
* No API to Subscription, Subscription Schedule; the API allows you to cancel them.
