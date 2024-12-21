FROM openjdk:17-jdk-slim-buster
WORKDIR /app
COPY /build/libs/service.jar .
EXPOSE 8088
ENTRYPOINT ["java", "-Xms512m", "-Xmx1024m", "-jar", "service.jar"]