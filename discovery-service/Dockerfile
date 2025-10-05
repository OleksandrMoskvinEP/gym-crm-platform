FROM maven:3.9.9-eclipse-temurin-21 AS build
LABEL authors="AlexM"

WORKDIR /app

COPY . .

RUN mvn clean package -pl discovery-service -am -DskipTests=true

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/discovery-service/target/*.jar app.jar

EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]