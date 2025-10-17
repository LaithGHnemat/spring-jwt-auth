FROM eclipse-temurin:17-jdk-alpine

RUN apk add --no-cache curl bash

WORKDIR /app

COPY target/*.jar app.jar

RUN curl -o wait-for-it.sh https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh \
    && chmod +x wait-for-it.sh

EXPOSE 8085
ENTRYPOINT ["./wait-for-it.sh", "mysqldb:3306", "-t", "60", "--", "./wait-for-it.sh", "redis:6379", "-t", "60", "--", "java", "-jar", "app.jar"]
