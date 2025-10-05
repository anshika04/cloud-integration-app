# Multi-stage build for Spring Boot application
FROM maven:3.9.8-eclipse-temurin-21 AS build

WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM openjdk:21

WORKDIR /app

# Install wget for health checks (OpenJDK 21 uses microdnf)
RUN microdnf install -y wget && microdnf clean all

# Copy the built jar
COPY --from=build /app/target/*.jar app.jar

# Set environment variables for Docker profile
ENV SPRING_PROFILES_ACTIVE=docker
ENV DB_HOST=postgres
ENV DB_PORT=5432
ENV DB_NAME=cloudintegration
ENV DB_USERNAME=postgres
ENV DB_PASSWORD=password

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/api/cloud/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
