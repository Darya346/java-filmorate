package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Primary
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film create(Film film) {
        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_rating_id) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);

        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        updateFilmGenres(film);
        return getById(film.getId()).orElse(null);
    }

    @Override
    public Film update(Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_rating_id = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        updateFilmGenres(film);
        return getById(film.getId()).orElse(null);
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM films WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public Optional<Film> getById(int id) {
        String sql = "SELECT f.*, m.name AS mpa_name " +
                "FROM films AS f " +
                "JOIN mpa_ratings AS m ON f.mpa_rating_id = m.id " +
                "WHERE f.id = ?";
        List<Film> films = jdbcTemplate.query(sql, this::mapRowToFilm, id);
        return films.stream().findFirst();
    }

    @Override
    public List<Film> getAll() {
        String sql = "SELECT f.*, m.name AS mpa_name " +
                "FROM films AS f " +
                "JOIN mpa_ratings AS m ON f.mpa_rating_id = m.id " +
                "ORDER BY f.id";
        return jdbcTemplate.query(sql, this::mapRowToFilm);
    }

    @Override
    public void addLike(int filmId, int userId) {
        String sql = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        String sql = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String sql = "SELECT f.id FROM films AS f " +
                "LEFT JOIN film_likes AS l ON f.id = l.film_id " +
                "GROUP BY f.id " +
                "ORDER BY COUNT(l.user_id) DESC " +
                "LIMIT ?";
        List<Integer> filmIds = jdbcTemplate.queryForList(sql, Integer.class, count);
        return filmIds.stream()
                .map(this::getById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private void updateFilmGenres(Film film) {
        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", film.getId());

        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return;
        }
        Set<Genre> uniqueGenres = new HashSet<>(film.getGenres());

        StringBuilder sqlBuilder = new StringBuilder("INSERT INTO film_genres (film_id, genre_id) VALUES ");
        List<String> values = uniqueGenres.stream()
                .map(genre -> String.format("(%d, %d)", film.getId(), genre.getId()))
                .collect(Collectors.toList());
        sqlBuilder.append(String.join(", ", values));

        jdbcTemplate.update(sqlBuilder.toString());
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getInt("id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        film.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
        film.setDuration(resultSet.getInt("duration"));
        film.setMpa(new MpaRating(resultSet.getInt("mpa_rating_id"), resultSet.getString("mpa_name")));

        String genresSql = "SELECT g.id, g.name FROM genres AS g JOIN film_genres AS fg ON g.id = fg.genre_id WHERE fg.film_id = ?";
        List<Genre> genres = jdbcTemplate.query(genresSql, (g_rs, g_rowNum) ->
                new Genre(g_rs.getInt("id"), g_rs.getString("name")), film.getId());
        film.getGenres().addAll(genres);

        return film;
    }
}