FROM maven:3.8-openjdk-17 AS build

WORKDIR /app

# Copiar apenas o pom.xml primeiro
COPY pom.xml .
COPY .mvn/ .mvn/
COPY mvnw mvnw
COPY mvnw.cmd mvnw.cmd

ENV MAVEN_OPTS="-Dmaven.repo.local=/root/.m2/repository -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=WARN"

# Baixar dependências e armazenar em cache
RUN --mount=type=cache,target=/root/.m2/repository mvn dependency:go-offline -B

# Copiar o código fonte
COPY src ./src

# Executar testes e build
RUN --mount=type=cache,target=/root/.m2/repository mvn clean verify \
    -B \
    -Dspring.profiles.active=test \
    -DCORS_ORIGIN=http://localhost:3000 \
    -Dmaven.test.failure.ignore=false \
    --no-transfer-progress
FROM openjdk:17-jdk-slim

WORKDIR /app

RUN apt-get update && \
    apt-get install -y tesseract-ocr tesseract-ocr-por && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

COPY --from=build /app/target/alfaeduca-0.0.1-SNAPSHOT.jar app.jar
COPY .env .env

EXPOSE 8081

ENTRYPOINT ["java","-Dspring.profiles.active=prod","-jar","app.jar"]