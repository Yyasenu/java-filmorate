Примеры запросов

Создание фильма:

INSERT INTO films (name,
                   description,
                   release_date,
                   duration_in_minutes,
                   mpa_rating_id)
VALUES (?, ?, ?, ?, ?);

Получение всех пользователей:

SELECT *
FROM users

Получение рейтинга MPA по идентификатору:

SELECT *
FROM mpa_ratings
WHERE mpa_rating_id = ?