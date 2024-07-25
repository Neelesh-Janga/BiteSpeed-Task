FROM openjdk:17-jdk-alpine
WORKDIR /Users/neelesh/IdeaProjects/Java/BiteSpeed/Identity-Reconciliation
COPY target/identity-reconciliation.jar /Identity-Reconciliation/identity-reconciliation.jar
EXPOSE 8080
RUN javac IdentityReconciliationApplication.java
CMD ["java", "IdentityReconciliationApplication"]
