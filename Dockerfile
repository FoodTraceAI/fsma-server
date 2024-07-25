#FROM openjdk:8-jdk-alpine
FROM amazoncorretto:21.0.2-alpine3.19
#RUN addgroup -S spring && adduser -S spring -G spring
#USER spring:spring
#ARG JAR_FILE=target/*.jar
ARG JAR_FILE=build/libs/fsma-server-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]