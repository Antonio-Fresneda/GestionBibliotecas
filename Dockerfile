FROM amazoncorretto:21-alpine-jdk
LABEL authors="Antonio"

COPY target/GestionBibliotecas-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar","/app.jar"]