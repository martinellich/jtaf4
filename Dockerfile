FROM azul/zulu-openjdk-alpine:25-jre

COPY target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
