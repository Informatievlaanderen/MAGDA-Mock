FROM vlaamseoverheid-magda-docker-releases.jfrog.io/magda/magda-openjdk-cacerts:${magda-openjdk.version}

VOLUME /tmp
COPY target/${project.build.finalName}.jar app.jar

EXPOSE 8080:8080
