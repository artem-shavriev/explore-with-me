#FROM eclipse-temurin:21-jre-jammy
#VOLUME /tmp
#ARG JAR_FILE=target/*.jar
#COPY ${JAR_FILE} app.jar
#ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar"]

FROM eclipse-temurin:21-jre-jammy
COPY target/ewm-service-*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]