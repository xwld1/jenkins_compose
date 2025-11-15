# build stage
FROM maven:3.9.5-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml ./
#COPY .mvn .mvn
COPY src src
RUN mvn -B -DskipTests package

# runtime stage
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/doggo-app-0.0.1-SNAPSHOT.jar app.jar
VOLUME ["/app/uploads", "/app/data"]
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
