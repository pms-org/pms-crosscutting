# ========= Stage 1: Build with Maven image =========
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy pom and sources
COPY pom.xml .
COPY src ./src

# Build the JAR
RUN mvn clean package -DskipTests

# ========= Stage 2: Run on slim JRE image =========
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Add non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy JAR from build stage
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENV JAVA_OPTS=""

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]