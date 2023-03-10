FROM openjdk:19-alpine
ARG JAR_FILE=build/libs/KITe_log-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
RUN apk add --no-cache gcompat
ENTRYPOINT ["java","-jar","/app.jar"]
