package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {

    private static final LocalDate EARLIEST_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;

    public Film create(Film film) {
        validateFilm(film);
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        getById(film.getId());
        validateFilm(film);
        return filmStorage.update(film);
    }

    public Film getById(int id) {
        return filmStorage.getById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Фильм с id=%d не найден.", id)));
    }

    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    public void addLike(int filmId, int userId) {
        getById(filmId);
        userStorage.getById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id=%d не найден.", userId)));
        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(int filmId, int userId) {
        getById(filmId);
        userStorage.getById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id=%d не найден.", userId)));
        filmStorage.removeLike(filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }

    private void validateFilm(Film film) {
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(EARLIEST_RELEASE_DATE)) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года.");
        }
        if (film.getMpa() != null) {
            mpaStorage.getById(film.getMpa().getId())
                    .orElseThrow(() -> new NotFoundException(String.format("Рейтинг MPA с id=%d не найден.", film.getMpa().getId())));
        }
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                genreStorage.getById(genre.getId())
                        .orElseThrow(() -> new NotFoundException(String.format("Жанр с id=%d не найден.", genre.getId())));
            }
        }
    }
}