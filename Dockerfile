FROM maven:3.6-jdk-8-alpine
WORKDIR /opt/cmad-simulator
COPY . .
RUN mvn clean compile assembly:single
CMD java -jar /opt/cmad-simulator/target/simulator-0.0.1-SNAPSHOT-jar-with-dependencies.jar
