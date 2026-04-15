package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmorateApplicationTest {

    @Autowired
    private UserController userController;

    @Autowired
    private FilmController filmController;

    @Test
    void testAddFriendWithUnknownUserIdThrowsNotFound() {
        assertThrows(EntityNotFoundException.class,
                () -> userController.addFriend(999L, 1L));
    }

    @Test
    void testAddFriendWithUnknownFriendIdThrowsNotFound() {
        assertThrows(EntityNotFoundException.class,
                () -> userController.addFriend(1L, 999L));
    }

    @Test
    void testGetFriendsOfNonExistentUserThrowsNotFound() {
        assertThrows(EntityNotFoundException.class,
                () -> userController.getUserFriends(999L));
    }

    @Test
    void testRemoveFriendWithUnknownUserIdThrowsNotFound() {
        assertThrows(EntityNotFoundException.class,
                () -> userController.removeFriend(999L, 1L));
    }

    @Test
    void testRemoveFriendWithUnknownFriendIdThrowsNotFound() {
        assertThrows(EntityNotFoundException.class,
                () -> userController.removeFriend(1L, 999L));
    }

    @Test
    void testCreateFilmWithEmptyNameThrowsValidation() {
        Film film = new Film();
        film.setDescription("Valid description");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(120);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmController.addFilm(film)
        );
        assertTrue(exception.getMessage().contains("Название не может быть пустым"));
    }

    @Test
    void testCreateFilmWithLongDescriptionThrowsValidation() {
        String longDescription = "a".repeat(201);
        Film film = new Film();
        film.setName("Valid Name");
        film.setDescription(longDescription);
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(120);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmController.addFilm(film)
        );
        assertTrue(exception.getMessage().contains("Описание не может быть длиннее 200 символов"));
    }

    @Test
    void testCreateFilmWithNegativeDurationThrowsValidation() {
        Film film = new Film();
        film.setName("Valid Name");
        film.setDescription("Valid description");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(-10);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmController.addFilm(film)
        );
        assertTrue(exception.getMessage().contains("Продолжительность должна быть положительным числом"));
    }

    @Test
    void testEmptyEmailThrowsException() {
        User user = new User();
        user.setLogin("testlogin");
        user.setBirthday(LocalDate.now());

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userController.createUser(user)
        );

        assertTrue(exception.getMessage().contains("Email"),
                "Сообщение об ошибке должно содержать 'Email'");
    }

    @Test
    void testValidUserDoesNotThrow() {
        User user = new User();
        user.setEmail("valid@example.com");
        user.setLogin("validlogin");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User createdUser = assertDoesNotThrow(() -> userController.createUser(user));

        assertNotNull(createdUser.getId());
        assertEquals("valid@example.com", createdUser.getEmail());
        assertEquals("validlogin", createdUser.getLogin());
    }
}

