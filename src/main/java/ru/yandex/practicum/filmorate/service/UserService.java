package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserStorage userStorage;
    private final Logger log = LoggerFactory.getLogger(UserController.class);

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            throw new ValidationException("Нельзя добавить себя в друзья");
        }

        User user = userStorage.getById(userId);
        User friend = userStorage.getById(friendId);

        if (user == null) {
            throw new EntityNotFoundException("Пользователь с id = " + userId + " не найден");
        }
        if (friend == null) {
            throw new EntityNotFoundException("Друг с id = " + friendId + " не найден");
        }
        if (user.getFriends().contains(friendId)) {
            return;
        }

        boolean userUpdated = user.getFriends().add(friendId);
        boolean friendUpdated = friend.getFriends().add(userId);

        try {
            if (userUpdated) {
                userStorage.update(user);
            }
            if (friendUpdated) {
                userStorage.update(friend);
            }
        } catch (Exception e) {
            if (userUpdated) user.getFriends().remove(friendId);
            if (friendUpdated) friend.getFriends().remove(userId);
            throw new EntityNotFoundException("Не удалось сохранить изменения друзей: " + e.getMessage());
        }
    }

    public void removeFriend(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            throw new ValidationException("Нельзя удалить себя из друзей");
        }

        User user = userStorage.getById(userId);
        User friend = userStorage.getById(friendId);

        if (user == null) {
            throw new EntityNotFoundException("Пользователь с id = " + userId + " не найден");
        }
        if (friend == null) {
            throw new EntityNotFoundException("Друг с id = " + friendId + " не найден");
        }

        boolean userUpdated = user.getFriends().remove(friendId);
        boolean friendUpdated = friend.getFriends().remove(userId);

        if (!userUpdated && !friendUpdated) {
            throw new EntityNotFoundException(
                    "Дружба между пользователями " + userId + " и " + friendId + " не найдена");
        }

        try {
            if (userUpdated) {
                userStorage.update(user);
            }
            if (friendUpdated) {
                userStorage.update(friend);
            }
        } catch (Exception e) {
            log.error("Ошибка при удалении дружбы между пользователями {} и {}", userId, friendId, e);
            if (userUpdated) user.getFriends().add(friendId);
            if (friendUpdated) friend.getFriends().add(userId);
            throw new RuntimeException(
                    "Ошибка сохранения изменений дружбы: " + e.getMessage(), e);
        }
    }

    public List<User> getCommonFriends(Long userId1, Long userId2) {
        User user1 = userStorage.getById(userId1);
        User user2 = userStorage.getById(userId2);

        if (user1 == null) {
            throw new EntityNotFoundException("Пользователь с id = " + userId1 + " не найден");
        }
        if (user2 == null) {
            throw new EntityNotFoundException("Пользователь с id = " + userId2 + " не найден");
        }

        Set<Long> commonFriendsIds = new HashSet<>(user1.getFriends());
        commonFriendsIds.retainAll(user2.getFriends());

        return commonFriendsIds.stream()
                .map(userStorage::getById)
                .filter(user -> user != null)
                .collect(Collectors.toList());
    }

    public Set<Long> getUserFriends(Long userId) {
        if (userId == null) {
            throw new ValidationException("ID пользователя не может быть null");
        }
        User user = userStorage.getById(userId);
        if (user == null) {
            throw new EntityNotFoundException("Пользователь с id = " + userId + " не найден");
        }
        return new HashSet<>(user.getFriends());
    }

    public List<User> getAllUsers() {
        return userStorage.getAll();
    }

    public User getUserById(Long id) {
        User user = userStorage.getById(id);
        if (user == null) {
            throw new EntityNotFoundException("Пользователь с id = " + id + " не найден");
        }
        return user;
    }

    public User createUser(User user) {
        validateUser(user);

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.add(user);
    }

    public User updateUser(User user) {
        if (user.getId() == null) {
            throw new ValidationException("ID пользователя не может быть null при обновлении");
        }
        validateUser(user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.update(user);
    }

    private void validateUser(User user) {
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getBirthday() == null) {
            throw new ValidationException("Дата рождения обязательна");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}
