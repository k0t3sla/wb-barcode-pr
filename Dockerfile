FROM openjdk:8-alpine

COPY target/uberjar/wb.jar /wb/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/wb/app.jar"]
