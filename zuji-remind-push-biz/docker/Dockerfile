FROM openjdk:8-jre AS PROD
RUN mkdir /app
COPY ../target/zuji-remind-push-biz-*.jar /app/app.jar
WORKDIR /app
EXPOSE 8080
CMD ["java", "-jar", "/app/app.jar"]