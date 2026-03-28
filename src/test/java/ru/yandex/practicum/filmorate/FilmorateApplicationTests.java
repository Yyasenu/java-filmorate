package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class SimpleValidationTests {

    private final UserController userController = new UserController();
    private final FilmController filmController = new FilmController();

    @Test
    void testEmptyEmailThrowsException() {
        User user = new User();
        user.setLogin("testlogin");
        user.setBirthday(LocalDate.now());

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userController.createUser(user)
        );
        assertTrue(exception.getMessage().contains("Email"));
    }

    @Test
    void testSpaceInLoginThrowsException() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("user name");
        user.setBirthday(LocalDate.now());

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userController.createUser(user)
        );
        assertTrue(exception.getMessage().contains("Логин"));
    }

    @Test
    void testFutureBirthdayThrowsException() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("validlogin");
        user.setBirthday(LocalDate.now().plusDays(1));

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userController.createUser(user)
        );
        assertTrue(exception.getMessage().contains("рождения"));
    }

    @Test
    void testValidUserDoesNotThrow() {
        User user = new User();
        user.setEmail("valid@example.com");
        user.setLogin("validlogin");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        assertDoesNotThrow(() -> userController.createUser(user));
    }

    @Test
    void testEmptyNameThrowsException() {
        Film film = new Film();
        film.setReleaseDate(LocalDate.now());
        film.setDuration(90);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmController.addFilm(film)
        );
        assertTrue(exception.getMessage().contains("Название"));
    }

    @Test
    void testLongDescriptionThrowsException() {
        Film film = new Film();
        film.setName("TestFilm");
        film.setDescription("a".repeat(201));
        film.setReleaseDate(LocalDate.now());
        film.setDuration(90);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmController.addFilm(film)
        );
        assertTrue(exception.getMessage().contains("Описание"));
    }

    @Test
    void testPastReleaseDateThrowsException() {
        Film film = new Film();
        film.setName("OldFilm");
        film.setReleaseDate(LocalDate.of(1890, 1, 1));
        film.setDuration(90);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmController.addFilm(film)
        );
        assertTrue(exception.getMessage().contains("релиза"));
    }

    @Test
    void testValidFilmDoesNotThrow() {
        Film film = new Film();
        film.setName("Valid Film");
        film.setDescription("Good description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(90);

        assertDoesNotThrow(() -> filmController.addFilm(film));
    }
}
