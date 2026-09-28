# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/attendance-0.0.1-SNAPSHOT.jar app.jar

CMD ["sh", "-c", "export KAFKA_SSL_TRUSTSTORE_CERTIFICATES=\"$(cat /etc/secrets/ca.pem)\" && exec java -jar app.jar"]