FROM openjdk:18.0.1-slim-bullseye

# Default to UTF-8 file.encoding
ENV LANG C.UTF-8

RUN mkdir -p /opt
ADD maven/opt/kitchen.jar /opt/application.jar

EXPOSE 8080

ENTRYPOINT java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 -jar /opt/application.jar -Djava.net.preferIPv4Stack=true