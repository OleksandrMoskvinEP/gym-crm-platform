FROM maven:3.9.9-eclipse-temurin-21 AS build
LABEL authors="AlexM"

WORKDIR /app

COPY . .

RUN mvn clean package -pl gateway-service -am -DskipTests=true spring-boot:repackage

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/gateway-service/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]