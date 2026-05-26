package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
        assertThatThrownBy(() -> controller.createUser(user))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Email must contain @");
    }

    @Test
    void shouldRejectInvalidEmail() {
        User user = new User(null, "mail.ru", "login", "Name", LocalDate.of(2000, 1, 1));
        assertThatThrownBy(() -> controller.createUser(user))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Email must contain @");
    }

    @Test
    void shouldRejectEmptyLogin() {
        User user = new User(null, "user@mail.ru", " ", "Name", LocalDate.of(2000, 1, 1));
        assertThatThrownBy(() -> controller.createUser(user))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Login must not be blank and must not contain spaces");
    }

    @Test
    void shouldRejectLoginWithWhitespace() {
        User user = new User(null, "user@mail.ru", "lo gin", "Name", LocalDate.of(2000, 1, 1));
        assertThatThrownBy(() -> controller.createUser(user))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Login must not be blank and must not contain spaces");
    }

    @Test
    void shouldRejectFutureBirthday() {
        User user = new User(null, "user@mail.ru", "login", "Name", LocalDate.now().plusDays(1));
        assertThatThrownBy(() -> controller.createUser(user))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Birthday must not be in the future");
    }

    @Test
    void shouldRejectEmptyUser() {
        assertThatThrownBy(() -> controller.createUser(null))
                .isInstanceOf(ValidationException.class)
                .hasMessage("User cannot be empty");
    }

    @Test
    void shouldRejectUserWithEmptyFields() {
        User user = new User();
        assertThatThrownBy(() -> controller.createUser(user))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Email must contain @");
    }
}
