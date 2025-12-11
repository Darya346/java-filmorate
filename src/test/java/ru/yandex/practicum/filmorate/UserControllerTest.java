package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private UserController userController;
    private User user;

    @BeforeEach
    void setUp() {
        userController = new UserController();
        user = new User();
        user.setLogin("validLogin");
        user.setEmail("valid@email.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));
    }

    // Тест на создание, когда имя пустое (должен использоваться логин)
    @Test
    void shouldSetNameAsLoginWhenNameIsBlank() {
        user.setName("");
        User createdUser = userController.createUser(user);
        assertEquals("validLogin", createdUser.getName());
    }

    // Тест на некорректный email
    @Test
    void shouldThrowExceptionWhenEmailIsInvalid() {
        user.setEmail("invalid-email.com");

        assertThrows(ValidationException.class, () -> {
            userController.createUser(user);
        });
    }

    // Тест на логин с пробелами
    @Test
    void shouldThrowExceptionWhenLoginHasSpaces() {
        user.setLogin("invalid login");

        assertThrows(ValidationException.class, () -> {
            userController.createUser(user);
        });
    }

    // Тест на дату рождения в будущем
    @Test
    void shouldThrowExceptionWhenBirthdayIsInTheFuture() {
        user.setBirthday(LocalDate.now().plusDays(1));

        assertThrows(ValidationException.class, () -> {
            userController.createUser(user);
        });
    }
}