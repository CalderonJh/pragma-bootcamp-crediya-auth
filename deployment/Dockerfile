# Use lightweight JDK image
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY build/libs/*.jar app.jar

# Optional: verify JAR
RUN java -Djarmode=layertools -jar app.jar extract || true

# Final runtime image
FROM eclipse-temurin:21-jre-alpine

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

WORKDIR /app

# Copy jar from builder stage
COPY --from=builder /app/app.jar app.jar

# JVM options
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]