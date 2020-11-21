FROM openjdk:8-jdk-alpine
MAINTAINER Sunil Rai <sunilrai7607@gmail.com>
VOLUME /app
ARG version
ENV version_number=$version
COPY ./build/libs/springboot-rediscache-api-$version_number.jar springboot-rediscache-api.jar
ENTRYPOINT ["java", "-jar","/springboot-rediscache-api.jar"]