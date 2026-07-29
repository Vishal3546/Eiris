# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
# Download dependencies first for caching
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# Copy the built jar file
COPY --from=build /app/target/*.jar app.jar

# Render assigns a dynamic port, so we expose it
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
