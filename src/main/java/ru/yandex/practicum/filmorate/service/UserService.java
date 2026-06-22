package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ErrorCode;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import java.time.LocalDate;
import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public Collection<User> getUsers() {
        log.info("Requested all users. Total users: {}", userStorage.getUserCount());
        return userStorage.getUsers();
    }

    public User getUser(Long id) {
        User user = userStorage.getUser(id);
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        log.info("Requested user with id={}", id);
        return user;
    }

    public User createUser(User user) {
        validateUser(user);
        userStorage.createUser(user);
        log.info("Created user with id={}", user.getId());
        return user;
    }

    public User updateUser(User user) {
        if (user.getId() == null) {
            throw new ValidationException(ErrorCode.EMPTY_USER_ID, "Id must not be null");
        }
        if (!userStorage.isUserExists(user.getId())) {
            throw new NotFoundException("User not found");
        }
        validateUser(user);
        userStorage.updateUser(user);
        log.info("Updated user with id={}", user.getId());
        return user;
    }

    public void addFriend(Long id, Long friendId) {
        User user = userStorage.getUser(id);
        User friend = userStorage.getUser(friendId);
        if (user == null || friend == null) {
            throw new NotFoundException("User not found");
        }
        if (user.getFriends().contains(friendId)) {
            throw new ValidationException(ErrorCode.USER_ALREADY_FRIEND, "User is already friend");
        }
        userStorage.addFriend(id, friendId);
        log.info("Added friend with id={} to user with id={}", friendId, id);
    }

    public void removeFriend(Long id, Long friendId) {
        User user = userStorage.getUser(id);
        User friend = userStorage.getUser(friendId);
        if (user == null || friend == null) {
            throw new NotFoundException("User not found");
        }
        userStorage.removeFriend(id, friendId);
        log.info("Removed friend with id={} from user with id={}", friendId, id);
    }

    public Collection<User> getFriends(Long id) {
        User user = userStorage.getUser(id);
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        log.info("Requested friends for user with id={}", id);
        return userStorage.getFriends(id);
    }

    public Collection<User> getCommonFriends(Long id, Long otherId) {
        User user = userStorage.getUser(id);
        User otherUser = userStorage.getUser(otherId);
        if (user == null || otherUser == null) {
            throw new NotFoundException("User not found");
        }
        log.info("Requested common friends for user with id={} and user with id={}", id, otherId);
        return userStorage.getCommonFriends(id, otherId);
    }

    private void validateUser(User user) {
        if (user == null) {
            log.warn("User validation failed: request body is empty");
            throw new ValidationException(ErrorCode.EMPTY_USER, "User cannot be empty");
        }
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.warn("User validation failed: email is empty or invalid");
            throw new ValidationException(ErrorCode.INVALID_USER_EMAIL, "Email must contain @");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.warn("User validation failed: login is empty or contains whitespace");
            throw new ValidationException(ErrorCode.INVALID_USER_LOGIN, "Login must not be blank and must not " +
                    "contain spaces");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("User name is empty. Login is used as name");
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("User validation failed: birthday is empty or in the future");
            throw new ValidationException(ErrorCode.INVALID_USER_BIRTHDAY, "Birthday must not be in the future");
        }
    }
}
