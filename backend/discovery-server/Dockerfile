FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml ./pom.xml
COPY discovery-server/pom.xml discovery-server/pom.xml
COPY auth-service/pom.xml auth-service/pom.xml
COPY employee-service/pom.xml employee-service/pom.xml
COPY asset-service/pom.xml asset-service/pom.xml
COPY api-gateway/pom.xml api-gateway/pom.xml
RUN mvn -q -DskipTests dependency:go-offline
COPY discovery-server/src discovery-server/src
RUN mvn -q -pl discovery-server -am package -DskipTests
FROM eclipse-temurin:17-jre-alpine
RUN apk add --no-cache curl \
    && addgroup -S app \
    && adduser -S -G app app
WORKDIR /app
COPY --from=build --chown=app:app /workspace/discovery-server/target/discovery-server-1.0.0.jar app.jar
USER app
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
