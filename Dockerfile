FROM ubuntu:latest AS build

RUN apt-get update
RUN apt-get install -y openjdk-17-jdk tesseract-ocr libtesseract-dev maven

COPY . .

RUN mvn clean install

FROM openjdk:17-jdk-slim

WORKDIR /app

EXPOSE 8081

COPY --from=build /target/alfaeduca-0.0.1-SNAPSHOT.jar app.jar
COPY .env .env

RUN apt-get update && apt-get install -y tesseract-ocr libtesseract-dev

ENTRYPOINT ["java","-jar","app.jar"]
