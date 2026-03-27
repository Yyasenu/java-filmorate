package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
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
        film.setDuration(Duration.ofMinutes(90));
        assertThrows(ValidationException.class, () -> filmController.validateFilm(film));
    }

    @Test
    void testFilmValidation_descriptionTooLong() {
        Film film = new Film();
        film.setName("TestFilm");
        film.setDescription("a".repeat(201)); // 201 символ
        film.setReleaseDate(LocalDate.now());
        film.setDuration(Duration.ofMinutes(90));
        assertThrows(ValidationException.class, () -> filmController.validateFilm(film));
    }

    @Test
    void testFilmValidation_releaseDateBefore1895() {
        Film film = new Film();
        film.setName("TestFilm");
        film.setReleaseDate(LocalDate.of(1890, 1, 1));
        film.setDuration(Duration.ofMinutes(90));
        assertThrows(ValidationException.class, () -> filmController.validateFilm(film));
    }
}
