package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ErrorCode;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class UserControllerTest {
    private UserController controller;

    @BeforeEach
    void setUp() {
        controller = new UserController();
    }

    @Test
    void shouldCreateUserOnBoundaryValidValues() {
        User user = new User(null, "user@mail.ru", "login", " ", LocalDate.now());
        User createdUser = controller.createUser(user);
        assertThat(createdUser.getId()).isEqualTo(1L);
        assertThat(createdUser.getName()).isEqualTo("login");
        assertThat(controller.getUsers()).containsExactly(createdUser);
    }

    @Test
    void shouldRejectEmptyEmail() {
        User user = new User(null, " ", "login", "Name", LocalDate.of(2000, 1, 1));
        assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> controller.createUser(user))
                .satisfies(exception -> assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_USER_EMAIL));
    }

    @Test
    void shouldRejectInvalidEmail() {
        User user = new User(null, "mail.ru", "login", "Name", LocalDate.of(2000, 1, 1));
        assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> controller.createUser(user))
                .satisfies(exception -> assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_USER_EMAIL));
    }

    @Test
    void shouldRejectEmptyLogin() {
        User user = new User(null, "user@mail.ru", " ", "Name", LocalDate.of(2000, 1, 1));
        assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> controller.createUser(user))
                .satisfies(exception -> assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_USER_LOGIN));
    }

    @Test
    void shouldRejectLoginWithWhitespace() {
        User user = new User(null, "user@mail.ru", "lo gin", "Name", LocalDate.of(2000, 1, 1));
        assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> controller.createUser(user))
                .satisfies(exception -> assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_USER_LOGIN));
    }

    @Test
    void shouldRejectFutureBirthday() {
        User user = new User(null, "user@mail.ru", "login", "Name", LocalDate.now().plusDays(1));
        assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> controller.createUser(user))
                .satisfies(exception -> assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_USER_BIRTHDAY));
    }

    @Test
    void shouldRejectEmptyUser() {
        assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> controller.createUser(null))
                .satisfies(exception -> assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.EMPTY_USER));
    }

    @Test
    void shouldRejectUserWithEmptyFields() {
        User user = new User();
        assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> controller.createUser(user))
                .satisfies(exception -> assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_USER_EMAIL));
    }
}
