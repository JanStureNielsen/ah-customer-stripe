
rm -f ./target/adhocmarkets-stripe-0.0.1-SNAPSHOT.jar
mvn clean package spring-boot:repackage
java -jar ./target/adhocmarkets-stripe-0.0.1-SNAPSHOT.jar
