
rm -f ./target/*.jar
mvn clean package spring-boot:repackage
java -jar ./target/*.jar
