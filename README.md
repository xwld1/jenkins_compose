Doggo History Service

Задание - собрать сервис в docker-compose и развётывать его с помощью jenkins
- клонируем репозиторий и пушим к себе в пространство
- В корне репозитория создаём Dockerfile для билда сервиса, используем multistage сборку
- в корне репозитория создаём директорию _devops 
- в _devops создаём две директории - jenkins и docker-compose 
- в директории jenkins создаём два jenkinsfile - 1 для развёртывания сервисов, 2 для заверешения работы сервисов
- в Jenkins создаём pipeline для развёртывания сервиса на своём пк и pipeline для завершения работы сервисов

Работа pipeline в Jenkinsfile:
1) клонируем репозиторий своего проекта
2) поднимаем docker-compose
3) post action - cleanWS 


Запуск локально (требуется Docker + Docker Compose):
1. docker compose up --build
2. Приложение доступно: http://localhost:8080

Эндпоинты:
GET  /dog/random      -- запрос к random.dog, сохраняет запись и возвращает DTO
GET  /dog/history     -- все записи
GET  /dog/history/{id}

Docker:
- build и run через docker compose (postgres:16)
- runtime образ: eclipse-temurin:17-jre-alpine


