package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
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
    void testEmptyEmailThrowsException() {
        User user = new User();
        user.setLogin("testlogin");
        user.setBirthday(LocalDate.now());

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.createUser(user));
        assertTrue(exception.getMessage().contains("Email"));
    }

    @Test
    void testSpaceInLoginThrowsException() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("user name");
        user.setBirthday(LocalDate.now());

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.createUser(user));
        assertTrue(exception.getMessage().contains("Логин"));
    }

    @Test
    void testFutureBirthdayThrowsException() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("validlogin");
        user.setBirthday(LocalDate.now().plusDays(1));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.createUser(user));
        assertTrue(exception.getMessage().contains("рождения"));
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

