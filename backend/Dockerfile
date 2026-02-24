# syntax=docker/dockerfile:1

FROM amazoncorretto:17-alpine AS build
WORKDIR /app

RUN apk add --no-cache maven

COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -q -DskipTests package

FROM amazoncorretto:17-alpine
WORKDIR /app

COPY --from=build /app/target/triage-backend-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health | grep -q '\"status\":\"UP\"' || exit 1
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
