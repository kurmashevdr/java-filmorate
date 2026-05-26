package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getUsers() {
        log.info("Requested all users. Total users: {}", users.size());
        return users.values();
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        validateUser(user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Created user with id={}", user.getId());
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        if (user.getId() == null) {
            throw new ValidationException("Id must not be null");
        }
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("User not found");
        }
        validateUser(user);
        users.put(user.getId(), user);
        log.info("Updated user with id={}", user.getId());
        return user;
    }

    private void validateUser(User user) {
        if (user == null) {
            log.warn("User validation failed: request body is empty");
            throw new ValidationException("User cannot be empty");
        }
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.warn("User validation failed: email is empty or invalid");
            throw new ValidationException("Email must contain @");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.warn("User validation failed: login is empty or contains whitespace");
            throw new ValidationException("Login must not be blank and must not contain spaces");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("User name is empty. Login is used as name");
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("User validation failed: birthday is empty or in the future");
            throw new ValidationException("Birthday must not be in the future");
        }
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
