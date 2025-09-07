# Multi-stage build optimized for Docker layer caching

# ========================
# Frontend Build Stage
# ========================
FROM node:22-alpine AS frontend-deps
WORKDIR /app/frontend

# Copy package files first (for dependency caching)
COPY frontend/package.json frontend/package-lock.json ./
RUN npm ci --only=production && npm cache clean --force

FROM node:22-alpine AS frontend-build
WORKDIR /app/frontend

# Copy node_modules from deps stage
COPY --from=frontend-deps /app/frontend/node_modules ./node_modules
COPY --from=frontend-deps /app/frontend/package*.json ./

# Copy source code and build
COPY frontend/ .
RUN npm run build

# ========================
# Backend Build Stage
# ========================
FROM gradle:8-jdk21 AS backend-deps
WORKDIR /app

# Copy Gradle files first (for dependency caching)
COPY build.gradle settings.gradle gradle.properties* ./
COPY gradle/ gradle/

# Download dependencies (this layer will be cached)
RUN gradle dependencies --no-daemon

FROM gradle:8-jdk21 AS backend-build
WORKDIR /app

# Copy cached dependencies and Gradle files
COPY --from=backend-deps /home/gradle/.gradle /home/gradle/.gradle
COPY build.gradle settings.gradle gradle.properties* ./
COPY gradle/ gradle/

# Copy source code
COPY src/ src/

# Copy built frontend
COPY --from=frontend-build /app/frontend/dist/ src/main/resources/static/

# Build the application (skip frontend since already built)
RUN gradle bootJar --no-daemon -PskipFrontend=true

# ========================
# Runtime Stage
# ========================
FROM eclipse-temurin:21-jre-jammy AS runtime
WORKDIR /app

# Install runtime dependencies in a single layer
RUN apt-get update && \
    apt-get install -y --no-install-recommends wget curl && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

# Create non-root user
RUN groupadd -r appuser && \
    useradd -r -g appuser appuser && \
    mkdir -p /app && \
    chown appuser:appuser /app

# Copy JAR file
COPY --from=backend-build /app/build/libs/*.jar app.jar
RUN chown appuser:appuser app.jar

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

USER appuser
EXPOSE 8080

# Use exec form for proper signal handling
ENTRYPOINT ["java", "-jar", "app.jar"]