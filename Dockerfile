FROM openjdk:17
ADD target/identity-reconciliation.jar identity-reconciliation.jar
ENTRYPOINT ["java","-jar","/identity-reconciliation.jar"]
