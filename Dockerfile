# Stage 1: build the app
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
COPY website website

RUN chmod +x mvnw && ./mvnw -q -DskipTests dependency:go-offline

COPY src src

RUN ./mvnw -q -DskipTests clean package

# Stage 2: runtime image
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
