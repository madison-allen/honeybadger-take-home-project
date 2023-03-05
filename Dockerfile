FROM maven:3.8.3-openjdk-17 AS build
COPY pom.xml pom.xml
COPY src src
RUN mvn clean package

FROM openjdk:17-oracle
COPY --from=build target/honeybadger-0.0.1-SNAPSHOT.jar .
ENTRYPOINT ["java","-jar","/honeybadger-0.0.1-SNAPSHOT.jar"]