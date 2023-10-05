FROM amazoncorretto:17.0.8-alpine3.18

WORKDIR /app

ARG JAR_FILE=api/build/libs/api-*.jar

EXPOSE 8080

COPY ${JAR_FILE} /app/app.jar

ENV SPRING_PROFILES_ACTIVE=docker

ENTRYPOINT ["java", "-jar", "./app.jar"]
