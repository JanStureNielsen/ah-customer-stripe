rm -f ./target/ah-customer-stripe-0.0.1-SNAPSHOT.jar
mvn clean package spring-boot:repackage
java -jar ./target/ah-customer-stripe-0.0.1-SNAPSHOT.jar
mvn clean package spring-boot:repackage
java -jar ./target/*.jar
