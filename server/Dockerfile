# Используем официальный образ OpenJDK 21
FROM eclipse-temurin:21-jdk

# Указываем рабочую директорию
WORKDIR /app

# Копируем JAR-файл сервера в контейнер
COPY target/shareIt-server-0.0.1-SNAPSHOT.jar app.jar

# Команда для запуска сервера
ENTRYPOINT ["java", "-jar", "app.jar"]