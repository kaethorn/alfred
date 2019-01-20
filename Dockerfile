FROM anapsix/alpine-java:8_jdk
VOLUME /tmp
EXPOSE 8080
ARG JAR_FILE=build/libs/komix.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
