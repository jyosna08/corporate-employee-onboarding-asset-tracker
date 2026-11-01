FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml ./pom.xml
COPY discovery-server/pom.xml discovery-server/pom.xml
COPY auth-service/pom.xml auth-service/pom.xml
COPY employee-service/pom.xml employee-service/pom.xml
COPY asset-service/pom.xml asset-service/pom.xml
COPY api-gateway/pom.xml api-gateway/pom.xml
RUN mvn -q -DskipTests dependency:go-offline
COPY api-gateway/src api-gateway/src
RUN mvn -q -pl api-gateway -am package -DskipTests
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/api-gateway/target/api-gateway-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
