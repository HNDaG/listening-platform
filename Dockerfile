FROM amazoncorretto:17.0.8-alpine3.18

WORKDIR /app

ARG JAR_FILE=build/libs/listeningplatform-*.jar

EXPOSE 8080

COPY ${JAR_FILE} ./app.jar

ENV SPRING_PROFILES_ACTIVE=docker

ENTRYPOINT ["java", "-jar", "./app.jar"]
