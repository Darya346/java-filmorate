# Проект Filmorate - Схема Базы Данных

## Схема БД (ER-диаграмма)

![ER-диаграмма для Filmorate](filmorate_database.png)

## Описание таблиц

Основу данных составляют таблицы `users` и `films`, которые хранят информацию о пользователях и фильмах соответственно.

Для хранения стандартизированных значений, таких как жанры и возрастные рейтинги MPA, были созданы отдельные таблицы-справочники: `genres` и `mpa_ratings`. Такой подход позволяет избежать дублирования данных и упрощает их обновление.

Связи "многие-ко-многим" реализуются через промежуточные таблицы. Таблица `film_genres` связывает фильмы с их жанрами. `film_likes` отслеживает, какие пользователи поставили лайки фильмам, что необходимо для расчета популярности. Таблица `friendships` реализует механику дружбы между пользователями, включая статус подтверждения (`status`).

## Примеры SQL-запросов

### Получение фильма со всеми его данными

Этот запрос собирает полную информацию о фильмах, включая название рейтинга MPA и список жанров, объединенных в одну строку.

```sql
SELECT
    f.film_id,
    f.name,
    f.description,
    f.release_date,
    f.duration,
    mr.rating_name AS mpa_rating,
    GROUP_CONCAT(g.genre_name SEPARATOR ', ') AS genres
FROM
    films AS f
JOIN
    mpa_ratings AS mr ON f.mpa_rating_id = mr.rating_id
LEFT JOIN
    film_genres AS fg ON f.film_id = fg.film_id
LEFT JOIN
    genres AS g ON fg.genre_id = g.genre_id
GROUP BY
    f.film_id;
```

### Запрос на получение 10 самых популярных фильмов

Популярность определяется количеством лайков. Фильмы сортируются по убыванию числа лайков.

```sql
SELECT
    f.film_id,
    f.name,
    COUNT(fl.user_id) AS likes_count
FROM
    films AS f
LEFT JOIN
    film_likes AS fl ON f.film_id = fl.film_id
GROUP BY
    f.film_id
ORDER BY
    likes_count DESC
LIMIT 10;
```

### Поиск общих друзей

Запрос для нахождения общих друзей между пользователями с ID 1 и 2.

```sql
SELECT
    u.*
FROM
    friendships AS f1
JOIN
    friendships AS f2 ON f1.friend_id = f2.friend_id
JOIN
    users AS u ON f1.friend_id = u.user_id
WHERE
    f1.user_id = 1 AND f2.user_id = 2
    AND f1.status = true AND f2.status = true;
```
