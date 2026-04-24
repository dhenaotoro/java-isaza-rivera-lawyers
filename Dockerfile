# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN apk add --no-cache curl tzdata
COPY build/libs/*.jar /tmp/libs/
RUN cp "$(ls /tmp/libs/*.jar | grep -v -- '-plain.jar' | head -n 1)" /app/app.jar && rm -rf /tmp/libs
EXPOSE 8081
# Set timezone to America/Bogota for scheduler
ENV TZ=America/Bogota
ENTRYPOINT ["java", "-jar", "app.jar"]
