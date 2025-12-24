package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Film {
    int id;

    @NotBlank(message = "Название не может быть пустым.")
    String name;

    @Size(max = 200, message = "Описание не должно превышать 200 символов.")
    String description;

    LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительной.")
    int duration;

    @JsonIgnore
    Set<Integer> likes = new HashSet<>();
}