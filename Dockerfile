FROM maven:3.8-openjdk-17 AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn clean install -DskipTests

FROM openjdk:17-jdk-slim

WORKDIR /app

RUN apt-get update && \
    apt-get install -y tesseract-ocr tesseract-ocr-por && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

COPY --from=build /app/target/alfaeduca-0.0.1-SNAPSHOT.jar app.jar
COPY .env .env

EXPOSE 8081

ENTRYPOINT ["java","-jar","app.jar"]