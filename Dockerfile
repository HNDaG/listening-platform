FROM amazoncorretto:17.0.8-alpine3.18

WORKDIR /app
ARG JAR_FILE=build/libs/listeningplatform-*.jar

COPY ${JAR_FILE} ./app.jar

ENTRYPOINT ["java", "-jar", "./app.jar"]
