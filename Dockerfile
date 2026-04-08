FROM eclipse-temurin:17-jdk AS build

WORKDIR /app
COPY gradle/ gradle/
COPY gradlew build.gradle.kts settings.gradle.kts ./
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon || true
COPY src/ src/
RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:17-jre

RUN groupadd -r appuser && useradd -r -g appuser -d /app -s /sbin/nologin appuser

WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
RUN chown -R appuser:appuser /app

USER appuser

ENV SPRING_DATA_MONGODB_HOST=localhost
ENV SPRING_DATA_MONGODB_PORT=27017
ENV SPRING_DATA_MONGODB_DATABASE=bankdb

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
