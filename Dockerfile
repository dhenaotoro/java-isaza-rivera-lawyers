# Build stage using official Gradle image (no gradlew required)
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /build
RUN apk add --no-cache unzip curl bash ca-certificates gradle
COPY . .
RUN gradle --no-daemon build -x test || gradle build -x test

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN apk add --no-cache curl
COPY --from=builder /build/build/libs/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
