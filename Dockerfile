FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY . .

# Baixar dependências e armazenar em cache
RUN --mount=type=cache,target=/root/.m2/repository mvn dependency:go-offline -B

# Executar testes e build
RUN --mount=type=cache,target=/root/.m2/repository mvn clean verify

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/target/alfaeduca-0.0.1-SNAPSHOT.jar app.jar


EXPOSE 8081

ENTRYPOINT ["java","-jar","app.jar"]