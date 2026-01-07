FROM maven:3.9-eclipse-temurin-21 AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean install



FROM eclipse-temurin:21-jre
COPY --from=build /home/app/target/IgorBot.jar /usr/local/lib/IgorBot.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/usr/local/lib/IgorBot.jar"]
