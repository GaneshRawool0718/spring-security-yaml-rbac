# Build stage
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create logs directory
RUN mkdir -p /app/logs

# Copy the built jar
COPY --from=build /app/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Environment variables (to be overridden by docker-compose)
ENV ISSUER_URL=""
ENV CLIENT_ID=""
ENV OKTA_API_TOKEN=""

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
