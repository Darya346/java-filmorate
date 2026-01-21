package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {

    private final FilmDbStorage filmStorage;
    private final UserStorage userStorage;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    void clearDb() {
        // Очищаем ВСЕ таблицы перед каждым тестом
        jdbcTemplate.update("DELETE FROM film_likes");
        jdbcTemplate.update("DELETE FROM film_genres");
        jdbcTemplate.update("DELETE FROM friendships");
        jdbcTemplate.update("DELETE FROM films");
        jdbcTemplate.update("DELETE FROM users");
        jdbcTemplate.execute("ALTER TABLE films ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE users ALTER COLUMN id RESTART WITH 1");
    }

    @Test
    public void testCreateAndGetFilmById() {
        Film film = createFilm(1);
        Film createdFilm = filmStorage.create(film);
        Optional<Film> filmOptional = filmStorage.getById(createdFilm.getId());

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(f -> {
                    assertThat(f).hasFieldOrPropertyWithValue("name", "Film 1");
                    assertThat(f.getMpa()).hasFieldOrPropertyWithValue("id", 1);
                });
    }

    @Test
    public void testGetAllFilms() {
        filmStorage.create(createFilm(1));
        filmStorage.create(createFilm(2));
        List<Film> films = filmStorage.getAll();
        assertThat(films).hasSize(2);
    }

    @Test
    public void testUpdateFilm() {
        Film film = filmStorage.create(createFilm(1));
        film.setName("Updated Film Name");
        filmStorage.update(film);
        Optional<Film> updatedFilmOpt = filmStorage.getById(film.getId());

        assertThat(updatedFilmOpt)
                .isPresent()
                .hasValueSatisfying(f -> assertThat(f).hasFieldOrPropertyWithValue("name", "Updated Film Name"));
    }

    @Test
    public void testDeleteFilm() {
        Film film = filmStorage.create(createFilm(1));
        filmStorage.delete(film.getId());
        Optional<Film> deletedFilmOpt = filmStorage.getById(film.getId());
        assertThat(deletedFilmOpt).isEmpty();
    }

    @Test
    public void testAddAndRemoveLike() {
        Film film = filmStorage.create(createFilm(1));
        User user = userStorage.create(createUser(1));

        filmStorage.addLike(film.getId(), user.getId());

        List<Film> popular = filmStorage.getPopularFilms(1);
        assertThat(popular).hasSize(1);
        assertThat(popular.get(0).getId()).isEqualTo(film.getId());

        filmStorage.removeLike(film.getId(), user.getId());
    }

    @Test
    public void testGetPopularFilms() {
        Film film1 = filmStorage.create(createFilm(1));
        Film film2 = filmStorage.create(createFilm(2));
        Film film3 = filmStorage.create(createFilm(3));
        User user1 = userStorage.create(createUser(1));
        User user2 = userStorage.create(createUser(2));

        filmStorage.addLike(film2.getId(), user1.getId());
        filmStorage.addLike(film3.getId(), user1.getId());
        filmStorage.addLike(film3.getId(), user2.getId());

        List<Film> popularFilms = filmStorage.getPopularFilms(3);

        assertThat(popularFilms).hasSize(3);
        assertThat(popularFilms.get(0).getId()).isEqualTo(film3.getId());
        assertThat(popularFilms.get(1).getId()).isEqualTo(film2.getId());
        assertThat(popularFilms.get(2).getId()).isEqualTo(film1.getId());
    }

    private Film createFilm(int index) {
        Film film = new Film();
        film.setName("Film " + index);
        film.setDescription("Description " + index);
        film.setReleaseDate(LocalDate.of(2000 + index, 1, 1));
        film.setDuration(100 + index);
        film.setMpa(new MpaRating(1, "G"));
        return film;
    }

    private User createUser(int index) {
        User user = new User();
        user.setEmail("user" + index + "@mail.com");
        user.setLogin("login" + index);
        user.setName("Name " + index);
        user.setBirthday(LocalDate.of(1990 + index, 1, 1));
        return user;
    }
}