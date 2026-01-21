package ru.yandex.practicum.filmorate.storage.dao;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GenreDbStorageTest {
    private final GenreDbStorage genreStorage;

    @Test
    void testGetAllGenres() {
        List<Genre> genres = genreStorage.getAll();
        assertThat(genres).hasSize(6); // По data.sql у нас 6 жанров
    }

    @Test
    void testGetGenreById() {
        Optional<Genre> genreOptional = genreStorage.getById(1);
        assertThat(genreOptional)
                .isPresent()
                .hasValueSatisfying(genre -> {
                    assertThat(genre).hasFieldOrPropertyWithValue("id", 1);
                    assertThat(genre).hasFieldOrPropertyWithValue("name", "Комедия");
                });
    }

    @Test
    void testGetGenreByNonExistentId() {
        Optional<Genre> genreOptional = genreStorage.getById(999);
        assertThat(genreOptional).isEmpty();
    }
}