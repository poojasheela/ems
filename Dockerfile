FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/ems-reactive-1.0.0.jar ems.jar

ENTRYPOINT ["java", "-jar", "ems.jar"]
