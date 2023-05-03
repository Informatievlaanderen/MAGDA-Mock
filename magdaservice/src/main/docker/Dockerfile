FROM eclipse-temurin:19.0.2_7-jre

VOLUME /tmp

EXPOSE 8080:8080

CMD ["java", "-jar", "/app.jar"]

COPY target/*.jar app.jar
