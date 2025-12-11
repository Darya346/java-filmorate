package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    private FilmController filmController;
    private Film film;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
        film = new Film();
        film.setName("Valid Name");
        film.setDescription("Valid description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(100);
    }

    // Тест на успешное создание
    @Test
    void shouldCreateFilmWhenDataIsValid() {
        Film createdFilm = filmController.createFilm(film);
        assertNotNull(createdFilm);
        assertEquals(1, filmController.getAllFilms().size());
    }

    // Тест на пустое имя
    @Test
    void shouldThrowExceptionWhenNameIsBlank() {
        film.setName("");

        assertThrows(ValidationException.class, () -> {
            filmController.createFilm(film);
        });
    }

    // Тест на слишком длинное описание
    @Test
    void shouldThrowExceptionWhenDescriptionIsTooLong() {
        String longDescription = "a".repeat(201);
        film.setDescription(longDescription);

        assertThrows(ValidationException.class, () -> {
            filmController.createFilm(film);
        });
    }

    // Тест на слишком старую дату релиза
    @Test
    void shouldThrowExceptionWhenReleaseDateIsTooEarly() {
        film.setReleaseDate(LocalDate.of(1895, 12, 27));

        assertThrows(ValidationException.class, () -> {
            filmController.createFilm(film);
        });
    }

    // Тест на отрицательную продолжительность
    @Test
    void shouldThrowExceptionWhenDurationIsNegative() {
        film.setDuration(-1);

        assertThrows(ValidationException.class, () -> {
            filmController.createFilm(film);
        });
    }
}