FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY . .
RUN chmod +x mvnw && ./mvnw -q -pl api-gateway -am package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /workspace/api-gateway/target/api-gateway-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
