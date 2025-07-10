FROM openjdk:17-jdk-slim
COPY build/libs/payment-svc-1.0-SNAPSHOT.jar /payment-svc.jar
ENTRYPOINT ["java","-jar","/payment-svc.jar"]