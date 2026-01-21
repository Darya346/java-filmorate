package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MpaDbStorageTest {

    private final MpaDbStorage mpaStorage;

    @Test
    void testGetAllMpaRatings() {
        List<MpaRating> ratings = mpaStorage.getAll();
        assertThat(ratings).hasSize(5); // По data.sql у нас 5 рейтингов
    }

    @Test
    void testGetMpaRatingById() {
        Optional<MpaRating> ratingOptional = mpaStorage.getById(1);
        assertThat(ratingOptional)
                .isPresent()
                .hasValueSatisfying(rating -> {
                    assertThat(rating).hasFieldOrPropertyWithValue("id", 1);
                    assertThat(rating).hasFieldOrPropertyWithValue("name", "G");
                });
    }

    @Test
    void testGetMpaRatingByNonExistentId() {
        Optional<MpaRating> ratingOptional = mpaStorage.getById(999);
        assertThat(ratingOptional).isEmpty();
    }
}