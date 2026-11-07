FROM eclipse-temurin:25-jdk-alpine
VOLUME /tmp
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 9999
ENTRYPOINT ["java", "-jar", "app.jar"]

