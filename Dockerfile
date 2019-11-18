FROM gradle:5.5-jdk11 as builder
WORKDIR /workspace/app
COPY settings.gradle build.gradle ./
RUN gradle dependencies
COPY src src
COPY ui ui
COPY config config
RUN gradle build unpack -x test -x check
WORKDIR build/dependency
RUN jar -xf ../libs/*.jar

FROM openjdk:11-jre
VOLUME /tmp
EXPOSE 8080
ARG DEPENDENCY=/workspace/app/build/dependency
COPY --from=builder ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=builder ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=builder ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","de.wasenweg.alfred.AlfredApplication"]
