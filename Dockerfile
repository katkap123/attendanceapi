FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY . .

RUN chmod +x mvnw

RUN ./mvnw clean package -DskipTests

CMD ["sh", "-c", "export KAFKA_SSL_TRUSTSTORE_CERTIFICATES=\"$(cat /etc/secrets/ca.pem)\" && exec java -jar target/attendance-0.0.1-SNAPSHOT.jar"]