FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app
COPY ./pom.xml /app
COPY ./src /app/src
COPY apache.conf /etc/apache2/sites-available/000-default.conf
RUN a2enmod rewrite
RUN mvn clean package -Dmaven.test.skip=true

FROM openjdk:17-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
