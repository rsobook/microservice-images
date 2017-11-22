FROM openjdk:8-jre-alpine

RUN mkdir /app

WORKDIR /app

ADD ./target/rsobook-image-ms-1.0.0-SNAPSHOT.jar /app

EXPOSE 8080

CMD ["java", "-jar", "rsobook-image-ms-1.0.0-SNAPSHOT.jar"]