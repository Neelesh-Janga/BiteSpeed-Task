FROM openjdk:17
WORKDIR /app
COPY target/identity-reconciliation.jar /app/identity-reconciliation.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/identity-reconciliation.jar"]
