FROM maven:3-openjdk-17
COPY . /usr/src/
WORKDIR /usr/src/
RUN mvn clean package
CMD ["java", "-jar", "target/guesser-1.0.jar"]