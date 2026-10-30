FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /workspace

COPY . .

ARG SERVICE_MODULE

RUN chmod +x mvnw \
    && ./mvnw -B -ntp -pl "${SERVICE_MODULE}" -am clean package -DskipTests \
    && cp "${SERVICE_MODULE}/target/${SERVICE_MODULE}-0.0.1-SNAPSHOT.jar" /app.jar

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
