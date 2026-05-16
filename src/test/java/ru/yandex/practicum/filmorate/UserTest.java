package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void createUserWithValidData_shouldReturnUserWithExpectedFields() {
        User user = new User(
                10,
                "mail@mail.ru",
                "dolore",
                "Nick Name",
                LocalDate.of(1946, 8, 20)
        );
        ResponseEntity<User> response = restTemplate.postForEntity("/users", user, User.class);

        User createdUser = response.getBody();
        assertEquals("mail@mail.ru", createdUser.getEmail());
        assertEquals("Nick Name", createdUser.getName());
        assertEquals("dolore", createdUser.getLogin());
        assertEquals(LocalDate.of(1946, 8, 20), createdUser.getBirthday());
    }

    @Test
    void createUserWithBadEmail_shouldShowErrorMessage() {
        User user = new User(10, "sashajaaa.ru", "sashajaaa", "Aleksandr", LocalDate.now().minusYears(35));
        ResponseEntity<User> response = restTemplate.postForEntity("/users", user, User.class);

        assertEquals("400 BAD_REQUEST", response.getStatusCode().toString());
    }

    @Test
    void createUserWithEmptyLogin_shouldShowErrorMessage() {
        User user = new User(10, "sashajaaa@yandex.ru", " ", "Aleksandr", LocalDate.now().minusYears(35));
        ResponseEntity<User> response = restTemplate.postForEntity("/users", user, User.class);

        assertEquals("400 BAD_REQUEST", response.getStatusCode().toString());
    }

    @Test
    void createFutureBirthUser_shouldShowErrorMessage() {
        User user = new User(10, "sashajaaa@yandex.ru", "sashajaaa", "Aleksandr", LocalDate.now().plusYears(35));
        ResponseEntity<User> response = restTemplate.postForEntity("/users", user, User.class);

        assertEquals("400 BAD_REQUEST", response.getStatusCode().toString());
    }

    @Test
    void updateUserToEmptyEmail_shouldShowErrorMessage() {
        User usr = new User(1, "sashajaaa@yandex.ru", "sashajaaa", "Aleksandr", LocalDate.now().minusYears(35));
        ResponseEntity<User> response = restTemplate.postForEntity("/users", usr, User.class);

        User user2 = new User(1, null, "sashajaaa", "Aleksandr", LocalDate.now().minusYears(35));
        HttpEntity<User> entity = new HttpEntity<>(user2);
        ResponseEntity<User> response2 = restTemplate.exchange("/users", HttpMethod.PUT, entity, User.class);

        assertEquals("400 BAD_REQUEST", response2.getStatusCode().toString());
    }

    @Test
    void updateUserToEmptyLogin_shouldShowErrorMessage() {
        User usr = new User(10, "sashajaaa@yandex.ru", "sashajaaa", "Aleksandr", LocalDate.now().minusYears(35));
        ResponseEntity<User> response = restTemplate.postForEntity("/users", usr, User.class);
        User user = new User(10, "sashajaaa@yandex.ru", " ", "Aleksandr", LocalDate.now().minusYears(35));
        HttpEntity<User> entity = new HttpEntity<>(user);
        ResponseEntity<User> response2 = restTemplate.exchange("/users", HttpMethod.PUT, entity, User.class);

        assertEquals("400 BAD_REQUEST", response2.getStatusCode().toString());
    }

    @Test
    void updateFutureBirthUser_shouldShowErrorMessage() {
        User usr = new User(10, "sashajaaa@yandex.ru", "sashajaaa", "Aleksandr", LocalDate.now().minusYears(35));
        ResponseEntity<User> response = restTemplate.postForEntity("/users", usr, User.class);
        User user = new User(10, "sashajaaa@yandex.ru", "sashajaaa", "Aleksandr", LocalDate.now().plusYears(35));
        HttpEntity<User> entity = new HttpEntity<>(user);
        ResponseEntity<User> response2 = restTemplate.exchange("/users", HttpMethod.PUT, entity, User.class);

        assertEquals("400 BAD_REQUEST", response2.getStatusCode().toString());
    }
}