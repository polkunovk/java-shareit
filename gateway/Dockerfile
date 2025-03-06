# Используем официальный образ OpenJDK 21
FROM eclipse-temurin:21-jdk

# Указываем рабочую директорию
WORKDIR /app

# Копируем JAR-файл gateway в контейнер
COPY target/shareIt-gateway-0.0.1-SNAPSHOT.jar app.jar

# Команда для запуска gateway
ENTRYPOINT ["java", "-jar", "app.jar"]