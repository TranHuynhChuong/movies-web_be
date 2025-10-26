# ----------- Build stage -----------
FROM maven:3.9.1-eclipse-temurin-21-jdk-alpine AS build

WORKDIR /app

# Copy file Maven để cache dependencies
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Cho phép mvnw chạy trong Linux
RUN chmod +x mvnw

# Tải dependency trước để cache layer Docker
RUN ./mvnw dependency:go-offline -B

# Copy toàn bộ source code
COPY src src

# Build jar Spring Boot
RUN ./mvnw clean package -DskipTests

# ----------- Run stage -----------
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy jar từ stage trước
COPY --from=build /app/target/movieweb-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8080

# Lệnh chạy
ENTRYPOINT ["java", "-jar", "app.jar"]
