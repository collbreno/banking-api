FROM maven:3.9.16-eclipse-temurin-21 AS builder

WORKDIR /workspace

# Resolve dependencies separately so source changes do not invalidate this layer.
COPY pom.xml ./
RUN mvn --batch-mode --no-transfer-progress dependency:go-offline

COPY src/ src/
RUN mvn --batch-mode --no-transfer-progress package -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /app
COPY --from=builder --chown=10001:10001 /workspace/target/visa-breno-*.jar app.jar

USER 10001:10001
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
