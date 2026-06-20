package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ErrorCode;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class UserControllerTest {
    private UserController controller;

    @BeforeEach
    void setUp() {
        UserService userService = new UserService(new InMemoryUserStorage());
        controller = new UserController(userService);
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
        User user = new User(null, " ", "login", "Name",
                LocalDate.of(2000, 1, 1));
        assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> controller.createUser(user))
                .satisfies(exception -> assertThat(exception.getErrorCode())
                        .isEqualTo(ErrorCode.INVALID_USER_EMAIL));
    }

    @Test
    void shouldRejectInvalidEmail() {
        User user = new User(null, "mail.ru", "login", "Name",
                LocalDate.of(2000, 1, 1));
        assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> controller.createUser(user))
                .satisfies(exception -> assertThat(exception.getErrorCode())
                        .isEqualTo(ErrorCode.INVALID_USER_EMAIL));
    }

    @Test
    void shouldRejectEmptyLogin() {
        User user = new User(null, "user@mail.ru", " ", "Name",
                LocalDate.of(2000, 1, 1));
        assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> controller.createUser(user))
                .satisfies(exception -> assertThat(exception.getErrorCode())
                        .isEqualTo(ErrorCode.INVALID_USER_LOGIN));
    }

    @Test
    void shouldRejectLoginWithWhitespace() {
        User user = new User(null, "user@mail.ru", "lo gin", "Name",
                LocalDate.of(2000, 1, 1));
        assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> controller.createUser(user))
                .satisfies(exception -> assertThat(exception.getErrorCode())
                        .isEqualTo(ErrorCode.INVALID_USER_LOGIN));
    }

    @Test
    void shouldRejectFutureBirthday() {
        User user = new User(null, "user@mail.ru", "login", "Name",
                LocalDate.now().plusDays(1));
        assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> controller.createUser(user))
                .satisfies(exception -> assertThat(exception.getErrorCode())
                        .isEqualTo(ErrorCode.INVALID_USER_BIRTHDAY));
    }

    @Test
    void shouldRejectEmptyUser() {
        assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> controller.createUser(null))
                .satisfies(exception -> assertThat(exception.getErrorCode())
                        .isEqualTo(ErrorCode.EMPTY_USER));
    }

    @Test
    void shouldRejectUserWithEmptyFields() {
        User user = new User();
        assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> controller.createUser(user))
                .satisfies(exception -> assertThat(exception.getErrorCode())
                        .isEqualTo(ErrorCode.INVALID_USER_EMAIL));
    }

    @Test
    void shouldAddFriendsForBothUsers() {
        User user = controller.createUser(new User(null, "user@mail.ru", "user", "User",
                LocalDate.of(2000, 1, 1)));
        User friend = controller.createUser(new User(null, "friend@mail.ru", "friend", "Friend",
                LocalDate.of(2001, 1, 1)));
        controller.addFriend(user.getId(), friend.getId());
        assertThat(controller.getUser(user.getId()).getFriends()).containsExactly(friend.getId());
        assertThat(controller.getUser(friend.getId()).getFriends()).containsExactly(user.getId());
        assertThat(controller.getFriends(user.getId())).containsExactly(friend);
        assertThat(controller.getFriends(friend.getId())).containsExactly(user);
    }

    @Test
    void shouldRemoveFriendsForBothUsers() {
        User user = controller.createUser(new User(null, "user@mail.ru", "user", "User",
                LocalDate.of(2000, 1, 1)));
        User friend = controller.createUser(new User(null, "friend@mail.ru", "friend", "Friend",
                LocalDate.of(2001, 1, 1)));
        controller.addFriend(user.getId(), friend.getId());
        controller.removeFriend(user.getId(), friend.getId());
        assertThat(controller.getUser(user.getId()).getFriends()).isEmpty();
        assertThat(controller.getUser(friend.getId()).getFriends()).isEmpty();
        assertThat(controller.getFriends(user.getId())).isEmpty();
        assertThat(controller.getFriends(friend.getId())).isEmpty();
    }

    @Test
    void shouldReturnCommonFriends() {
        User firstUser = controller.createUser(new User(null, "first@mail.ru", "first", "First",
                LocalDate.of(2000, 1, 1)));
        User secondUser = controller.createUser(new User(null, "second@mail.ru", "second",
                "Second", LocalDate.of(2001, 1, 1)));
        User commonFriend = controller.createUser(new User(null, "common@mail.ru", "common",
                "Common", LocalDate.of(2002, 1, 1)));
        controller.addFriend(firstUser.getId(), commonFriend.getId());
        controller.addFriend(secondUser.getId(), commonFriend.getId());
        assertThat(controller.getCommonFriends(firstUser.getId(), secondUser.getId())).containsExactly(commonFriend);
    }
}
