FROM openjdk:17-alpine

WORKDIR /usr/src/app

ARG JAR_PATH=./build/libs

COPY ${JAR_PATH}/config-service-0.0.1-SNAPSHOT.jar ${JAR_PATH}/config-service-0.0.1-SNAPSHOT.jar

RUN mkdir -p /root/.ssh/
COPY id_ed25519 /root/.ssh/id_ed25519
COPY known_hosts /root/.ssh/known_hosts
RUN chmod 600 /root/.ssh && \
    echo "Host *\n\tStrictHostKeyChecking no\n\n" > /root/.ssh/config

CMD ["java","-jar","./build/libs/config-service-0.0.1-SNAPSHOT.jar"]
