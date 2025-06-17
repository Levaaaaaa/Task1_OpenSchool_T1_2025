FROM gradle:8.5-jdk21 AS build
WORKDIR /app

# Корневые файлы
COPY settings.gradle .
COPY gradlew .
COPY gradlew.bat .
COPY gradle/ ./gradle

# Модули
COPY my-spring-starter/ ./my-spring-starter
COPY module1/ ./module1

# Сборка с wrapper-ом
RUN ./gradlew :module1:build -x test

FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/module1/build/libs/*.jar app.jar
CMD ["java", "-jar", "app.jar"]
EXPOSE 8080
