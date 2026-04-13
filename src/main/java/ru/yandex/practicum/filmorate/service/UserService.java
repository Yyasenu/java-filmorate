package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(Long userId, Long friendId) {
        User user = userStorage.getById(userId);
        User friend = userStorage.getById(friendId);

        if (user == null || friend == null) {
            throw new ValidationException("Пользователь или друг не найден");
        }

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);

        userStorage.update(user);
        userStorage.update(friend);
    }

    public void removeFriend(Long userId, Long friendId) {
        User user = userStorage.getById(userId);
        User friend = userStorage.getById(friendId);

        if (user != null && friend != null) {
            user.getFriends().remove(friendId);
            friend.getFriends().remove(userId);

            userStorage.update(user);
            userStorage.update(friend);
        }
    }

    public List<User> getCommonFriends(Long userId1, Long userId2) {
        User user1 = userStorage.getById(userId1);
        User user2 = userStorage.getById(userId2);

        if (user1 == null || user2 == null) {
            return new ArrayList<>();
        }

        Set<Long> commonFriendsIds = new HashSet<>(user1.getFriends());
        commonFriendsIds.retainAll(user2.getFriends());

        return commonFriendsIds.stream()
                .map(userStorage::getById)
                .filter(user -> user != null)
                .collect(Collectors.toList());
    }

    public Set<Long> getUserFriends(Long userId) {
        User user = userStorage.getById(userId);
        if (user == null) {
            throw new ValidationException("Пользователь не найден");
        }
        return user.getFriends();
    }

    public List<User> getAllUsers() {
        return userStorage.getAll();
    }

    public User getUserById(Long id) {
        return userStorage.getById(id);
    }

    public User createUser(User user) {
        return userStorage.add(user);
    }

    public User updateUser(User user) {
        return userStorage.update(user);
    }
}

