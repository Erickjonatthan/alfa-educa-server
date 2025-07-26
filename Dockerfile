FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copiar apenas o pom.xml primeiro
COPY pom.xml .
COPY .mvn/ .mvn/
COPY mvnw mvnw
COPY mvnw.cmd mvnw.cmd

# Baixar dependências e armazenar em cache
RUN --mount=type=cache,target=/root/.m2/repository mvn dependency:go-offline -B

# Copiar o código fonte
COPY src ./src

# Executar testes e build
RUN --mount=type=cache,target=/root/.m2/repository mvn clean verify \
    -B \
    -Dspring.profiles.active=test \
    -Dmaven.test.failure.ignore=false \
    --no-transfer-progress

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/target/alfaeduca-0.0.1-SNAPSHOT.jar app.jar
COPY .env .env

EXPOSE 8081

ENTRYPOINT ["java","-Dspring.profiles.active=prod","-jar","app.jar"]