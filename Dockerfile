FROM amazoncorretto:17.0.7-al2023-headless
LABEL   "title"="spring" \
        "version"="1.0"

COPY ./build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]