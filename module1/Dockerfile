FROM gradle:8.5-jdk21 AS build
WORKDIR /app
COPY . .
COPY --from=starter-build /root/.m2 /root/.m2
RUN gradle build -x test

FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
CMD ["java", "-jar", "app.jar"]
EXPOSE 8080