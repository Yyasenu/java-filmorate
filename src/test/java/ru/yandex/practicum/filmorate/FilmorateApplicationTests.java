package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;

import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class FilmorateApplicationTests {

    private final UserController userController = new UserController();
    private final FilmController filmController = new FilmController();

    @Test
    void testUserValidation_emptyEmail() {
        User user = new User();
        user.setLogin("test");
        user.setBirthday(LocalDate.now());
        assertThrows(ValidationException.class, () -> userController.validateUser(user));
    }

    @Test
    void testUserValidation_loginWithSpace() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("user name");
        user.setBirthday(LocalDate.now());
        assertThrows(ValidationException.class, () -> userController.validateUser(user));
    }

    @Test
    void testUserValidation_futureBirthday() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("user123");
        user.setBirthday(LocalDate.now().plusDays(1));
        assertThrows(ValidationException.class, () -> userController.validateUser(user));
    }

    @Test
    void testFilmValidation_emptyName() {
        Film film = new Film();
        film.setReleaseDate(LocalDate.now());
        film.setDuration(90 * 60);
        assertThrows(ValidationException.class, () -> filmController.validateFilm(film));
    }

    @Test
    void testFilmValidation_descriptionTooLong() {
        Film film = new Film();
        film.setName("TestFilm");
        film.setDescription("a".repeat(201));
        film.setReleaseDate(LocalDate.now());
        film.setDuration(90 * 60);
        assertThrows(ValidationException.class, () -> filmController.validateFilm(film));
    }

    @Test
    void testFilmValidation_releaseDateBefore1895() {
        Film film = new Film();
        film.setName("TestFilm");
        film.setReleaseDate(LocalDate.of(1890, 1, 1));
        film.setDuration(90 * 60);
        assertThrows(ValidationException.class, () -> filmController.validateFilm(film));
    }

    @Test
    void testUserValidation_validUser() {
        User user = new User();
        user.setEmail("valid@example.com");
        user.setLogin("validlogin");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userController.validateUser(user);
    }
}
