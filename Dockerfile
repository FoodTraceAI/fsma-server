FROM amazoncorretto:21.0.2-alpine3.19
RUN addgroup -S AppUser && adduser -S AppUser -G AppUser
USER AppUser
#ARG JAR_FILE=build/libs/*.jar
ARG JAR_FILE=build/libs/fsma-server-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]