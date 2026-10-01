# Travel Assistant

Веб-приложение для подбора туров: агрегирует данные об отелях, авиаперелётах, 
ресторанах и достопримечательностях из нескольких внешних API.

## Функционал

- Поиск отелей через Hotelbeds API с кешированием контента в PostgreSQL
- Поиск авиаперелётов через Duffel API
- Поиск ресторанов и достопримечательностей через Google Places API
- Конвертация цен в рубли по курсу ЦБ РФ
- Регистрация и авторизация пользователей (Spring Security)
- Избранное (отели, рестораны, достопримечательности)
- Оформление заказов (тур = отель + номер + перелёт)

## Технологии

**Backend:** Java, Spring Boot, Spring Security, Spring Data JPA, PostgreSQL, 
WebClient (Reactor)

**Frontend:** HTML, CSS, JavaScript

**Инфраструктура:** Docker, Docker Compose

## Структура проекта

```
travel-project/
├── backend/      # Spring Boot приложение
├── frontend/     # HTML/CSS/JS
└── docker-compose.yml
```

## Запуск проекта

### Требования
- Java 21+
- Docker и Docker Compose
- API-ключи: Google Places, Duffel (test mode), Hotelbeds (test mode)

### Шаги

1. Склонировать репозиторий:
```bash
git clone https://github.com/lukinadiana-creator/travel-project.git
cd travel-project
```

2. Скопировать `.env.example` в `.env` и заполнить своими ключами:
```bash
cp .env.example .env
```

3. Поднять базу данных:
```bash
docker-compose up -d
```

4. Запустить backend:
```bash
cd backend
./mvnw spring-boot:run
```

5. Открыть `frontend/index.html` в браузере (например, через расширение Live Server в VS Code)
