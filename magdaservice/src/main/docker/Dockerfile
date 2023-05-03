#FROM vlaamseoverheid-magda-docker-releases.jfrog.io/magda/magda-openjdk-cacerts:${magda-openjdk.version}
FROM eclipse-temurin:${magda-openjdk.version}

VOLUME /tmp

EXPOSE 8080:8080

CMD ["java", "-jar", "/app.jar"]

COPY target/${project.build.finalName}.jar app.jar
