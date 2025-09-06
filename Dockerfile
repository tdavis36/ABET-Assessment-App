# This Dockerfile works with Spring Boot's bootBuildImage task
# The image is built using Cloud Native Buildpacks

FROM paketobuildpacks/builder-jammy-base:latest as builder

# Copy the built JAR file (built by bootBuildImage)
# This is a placeholder - the actual image will be built by Gradle
COPY build/libs/*.jar app.jar

# The buildpack will handle the rest automatically
ENTRYPOINT ["java", "-jar", "/app.jar"]

# For manual building without buildpack, use this multi-stage approach:
FROM node:18.17.0-alpine AS frontend-build
WORKDIR /app/frontend
COPY frontend/package*.json ./
RUN npm ci
COPY frontend/ .
RUN npm run build

FROM gradle:8-jdk21 AS backend-build
WORKDIR /app
COPY build.gradle settings.gradle ./
COPY src/ src/
COPY --from=frontend-build /app/frontend/dist/ src/main/resources/static/
RUN gradle bootJar --no-daemon

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Install wget for health checks
RUN apt-get update && \
    apt-get install -y wget && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Create non-root user
RUN groupadd -r appuser && useradd -r -g appuser appuser

COPY --from=backend-build /app/build/libs/*.jar app.jar
RUN chown appuser:appuser app.jar

USER appuser
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]