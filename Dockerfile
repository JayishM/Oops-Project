# Build the Spring Boot API with Java 17 and Maven.
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY backend/pom.xml backend/pom.xml
COPY backend/src backend/src
RUN mvn -B -f backend/pom.xml -DskipTests package

# Small runtime image; run as a non-root user.
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
RUN mkdir -p /app/uploads && chown -R 10001:10001 /app
COPY --from=build --chown=10001:10001 /workspace/backend/target/nightmarket-backend-0.0.1-SNAPSHOT.jar /app/app.jar
USER 10001:10001
ENV PORT=8080
ENV UPLOAD_DIR=/app/uploads
ENV SEED_DATA_ENABLED=true
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "exec java -Dserver.port=${PORT:-8080} -jar /app/app.jar"]
