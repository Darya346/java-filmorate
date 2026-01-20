package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {

    private final UserDbStorage userStorage;
    private final JdbcTemplate jdbcTemplate;

    // Этот метод будет очищать таблицы перед каждым тестом
    @BeforeEach
    void clearDb() {
        jdbcTemplate.update("DELETE FROM friendships");
        jdbcTemplate.update("DELETE FROM users");
        jdbcTemplate.execute("ALTER TABLE users ALTER COLUMN id RESTART WITH 1");
    }

    @Test
    public void testCreateAndGetUserById() {
        User user = createUser(1);
        User createdUser = userStorage.create(user);

        Optional<User> userOptional = userStorage.getById(createdUser.getId());

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(u -> {
                    assertThat(u).hasFieldOrPropertyWithValue("id", createdUser.getId());
                    assertThat(u).hasFieldOrPropertyWithValue("email", "user1@mail.com");
                });
    }

    @Test
    public void testGetAllUsers() {
        userStorage.create(createUser(1));
        userStorage.create(createUser(2));
        List<User> users = userStorage.getAll();
        assertThat(users).hasSize(2);
    }

    @Test
    public void testUpdateUser() {
        User user = userStorage.create(createUser(1));
        user.setName("Updated Name");
        userStorage.update(user);
        Optional<User> updatedUserOpt = userStorage.getById(user.getId());

        assertThat(updatedUserOpt)
                .isPresent()
                .hasValueSatisfying(u -> assertThat(u).hasFieldOrPropertyWithValue("name", "Updated Name"));
    }

    @Test
    public void testDeleteUser() {
        User user = userStorage.create(createUser(1));
        userStorage.delete(user.getId());
        Optional<User> deletedUserOpt = userStorage.getById(user.getId());
        assertThat(deletedUserOpt).isEmpty();
    }

    @Test
    public void testAddAndGetFriends() {
        User user1 = userStorage.create(createUser(1));
        User user2 = userStorage.create(createUser(2));
        userStorage.addFriend(user1.getId(), user2.getId());

        List<User> friends = userStorage.getFriends(user1.getId());
        assertThat(friends).hasSize(1);
        assertThat(friends.get(0).getId()).isEqualTo(user2.getId());
    }

    @Test
    public void testRemoveFriend() {
        User user1 = userStorage.create(createUser(1));
        User user2 = userStorage.create(createUser(2));
        userStorage.addFriend(user1.getId(), user2.getId());
        userStorage.removeFriend(user1.getId(), user2.getId());

        List<User> friends = userStorage.getFriends(user1.getId());
        assertThat(friends).isEmpty();
    }

    @Test
    public void testGetCommonFriends() {
        User user1 = userStorage.create(createUser(1));
        User user2 = userStorage.create(createUser(2));
        User commonFriend = userStorage.create(createUser(3));
        userStorage.addFriend(user1.getId(), commonFriend.getId());
        userStorage.addFriend(user2.getId(), commonFriend.getId());

        List<User> commonFriends = userStorage.getCommonFriends(user1.getId(), user2.getId());
        assertThat(commonFriends).hasSize(1);
        assertThat(commonFriends.get(0).getId()).isEqualTo(commonFriend.getId());
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