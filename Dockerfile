FROM amazoncorretto:17.0.8-alpine3.18
LABEL   "title"="spring" \
        "version"="1.0"

COPY build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar", "--spring.profiles.active=${SERVERMODE}"]