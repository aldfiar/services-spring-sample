## Spring Boot Sample
Приложение на Spring Boot
### Запуск
Для запуска через docker-compose:\
`docker-compose up -d`\
Порт по умолчанию 9800.

Для запуска напрямую, необходимо внести данные о CУБД в application.properties в resource директории. По умолчанию используется PostgreSQL\
Собрать `mvn package` (в случае поднятой БД внести ее параметры в application.properties в /test/resources или использовать `-DskipTests`) и запустить проект `java -jar target/services-spring-sample.jar`

### Доступные эндпоинты:
Создание пользователя:\
`POST {port}/api/register`\
Получение токена для зарегистрированного пользователя:\
`POST {port}/message/token`\
Запись сообщения в базу:\
`POST {port}/message/send`\
Получение истории:\
`POST {port}/message/history`\

По умолчанию токен выдается на час