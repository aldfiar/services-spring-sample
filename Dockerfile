FROM openjdk:8-jre-alpine
MAINTAINER aldfiar

COPY target/services-spring-sample.jar application.jar

EXPOSE 9800

ENTRYPOINT ["java", "-jar", "application.jar"]