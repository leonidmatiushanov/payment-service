FROM maven:3.9-eclipse-temurin-21-alpine as build
WORKDIR /app

COPY pom.xml .
RUN mvn -q -e -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -q -DskipTests package

# Используем минимальный runtime-образ с JDK 21
FROM eclipse-temurin:21-jre-alpine

# Создаём рабочую директорию приложения
WORKDIR /app

# Копируем jar-файл в контейнер
COPY --from=build /app/target/*.jar app.jar

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

