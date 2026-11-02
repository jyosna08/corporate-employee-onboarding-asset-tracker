FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml ./pom.xml
COPY discovery-server/pom.xml discovery-server/pom.xml
COPY auth-service/pom.xml auth-service/pom.xml
COPY employee-service/pom.xml employee-service/pom.xml
COPY asset-service/pom.xml asset-service/pom.xml
COPY api-gateway/pom.xml api-gateway/pom.xml
RUN mvn -q -DskipTests dependency:go-offline
COPY employee-service/src employee-service/src
RUN mvn -q -pl employee-service -am package -DskipTests
FROM eclipse-temurin:17-jre-alpine
RUN apk add --no-cache curl \
    && addgroup -S app \
    && adduser -S -G app app
WORKDIR /app
COPY --from=build --chown=app:app /workspace/employee-service/target/employee-service-1.0.0.jar app.jar
USER app
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
