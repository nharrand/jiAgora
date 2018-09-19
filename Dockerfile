FROM ibmjava

COPY target/jiAgora-1.0-SNAPSHOT-jar-with-dependencies.jar /app/jiAgora.jar
COPY resources /app/resources

EXPOSE 48000

CMD ["java", "-jar", "/app/jiAgora.jar"]
