package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    int id;

    @NotBlank(message = "Email не может быть пустым.")
    @Email(message = "Email должен быть в правильном формате.")
    String email;

    @NotBlank(message = "Логин не может быть пустым.")
    @Pattern(regexp = "\\S*", message = "Логин не может содержать пробелы.")
    String login;

    String name;

    @PastOrPresent(message = "Дата рождения не может быть в будущем.")
    LocalDate birthday;

    @JsonIgnore
    Set<Integer> friends = new HashSet<>();
}