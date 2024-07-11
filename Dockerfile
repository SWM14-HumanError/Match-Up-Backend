FROM amazoncorretto:17.0.8-alpine3.18
LABEL   "title"="spring" \
        "version"="1.0"

COPY build/libs/*.jar app.jar
ENV TZ=Asia/Seoul
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
ENTRYPOINT ["java","-jar","app.jar", "--spring.profiles.active=dev"]