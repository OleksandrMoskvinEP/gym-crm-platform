FROM maven:3.9.9-eclipse-temurin-21 AS build
LABEL authors="AlexM"

WORKDIR /app

COPY . .

RUN mvn clean package -pl gym-crm-core -am -DskipTests=true

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/gym-crm-core/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]