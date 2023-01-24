FROM vlaamseoverheid-magda-docker-releases.jfrog.io/magda/magda-openjdk-cacerts:${magda-openjdk.version}

VOLUME /tmp
COPY target/${project.build.finalName}-jar-with-dependencies.jar app.jar
