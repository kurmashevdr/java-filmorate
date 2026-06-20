package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ErrorCode;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class FilmControllerTest {
    private FilmController controller;
    private InMemoryUserStorage userStorage;

    @BeforeEach
    void setUp() {
        userStorage = new InMemoryUserStorage();
        controller = new FilmController(new FilmService(new InMemoryFilmStorage(), userStorage));
    }

    @Test
    void shouldCreateFilmOnBoundaryValidValues() {
        Film film = new Film(
                null,
                "Lumiere",
                "a".repeat(200),
                LocalDate.of(1895, 12, 28),
                1
        );
        Film createdFilm = controller.createFilm(film);
        assertThat(createdFilm.getId()).isEqualTo(1L);
        assertThat(controller.getFilms()).containsExactly(createdFilm);
    }

    @Test
    void shouldGetFilmById() {
        Film film = controller.createFilm(new Film(null, "Film", "Description",
                LocalDate.of(2000, 1, 1), 100));
        Film foundFilm = controller.getFilm(film.getId());
        assertThat(foundFilm).isEqualTo(film);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenFilmDoesNotExist() {
        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> controller.getFilm(1L));
    }

    @Test
    void shouldRejectEmptyName() {
        Film film = new Film(null, " ", "Description",
                LocalDate.of(2000, 1, 1), 100);
        assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> controller.createFilm(film))
                .satisfies(exception -> assertThat(exception.getErrorCode())
                        .isEqualTo(ErrorCode.EMPTY_FILM_NAME));
    }

    @Test
    void shouldRejectTooLongDescription() {
        Film film = new Film(null, "Film", "a".repeat(201),
                LocalDate.of(2000, 1, 1), 100);
        assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> controller.createFilm(film))
                .satisfies(exception -> assertThat(exception.getErrorCode())
                        .isEqualTo(ErrorCode.TOO_LONG_FILM_DESCRIPTION));
    }

    @Test
    void shouldRejectReleaseDateBeforeCinemaBirthday() {
        Film film = new Film(null, "Film", "Description",
                LocalDate.of(1895, 12, 27), 100);
        assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> controller.createFilm(film))
                .satisfies(exception -> assertThat(exception.getErrorCode())
                        .isEqualTo(ErrorCode.INVALID_FILM_RELEASE_DATE));
    }

    @Test
    void shouldRejectNonPositiveDuration() {
        Film film = new Film(null, "Film", "Description",
                LocalDate.of(2000, 1, 1), 0);
        assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> controller.createFilm(film))
                .satisfies(exception -> assertThat(exception.getErrorCode())
                        .isEqualTo(ErrorCode.NON_POSITIVE_FILM_DURATION));
    }

    @Test
    void shouldRejectEmptyFilm() {
        assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> controller.createFilm(null))
                .satisfies(exception -> assertThat(exception.getErrorCode())
                        .isEqualTo(ErrorCode.EMPTY_FILM));
    }

    @Test
    void shouldRejectFilmWithEmptyFields() {
        Film film = new Film();

        assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> controller.createFilm(film))
                .satisfies(exception -> assertThat(exception.getErrorCode())
                        .isEqualTo(ErrorCode.EMPTY_FILM_NAME));
    }

    @Test
    void shouldAddLikeToFilm() {
        Film film = controller.createFilm(new Film(null, "Film", "Description",
                LocalDate.of(2000, 1, 1), 100));
        User user = createUser("user@mail.ru", "user");
        controller.likeFilm(film.getId(), user.getId());
        assertThat(controller.getFilm(film.getId()).getLikesByUsers()).containsExactly(user.getId());
        assertThat(controller.getFilm(film.getId()).getLikesCount()).isEqualTo(1);
    }

    @Test
    void shouldNotAddDuplicateLikeToFilm() {
        Film film = controller.createFilm(new Film(null, "Film", "Description",
                LocalDate.of(2000, 1, 1), 100));
        User user = createUser("user@mail.ru", "user");
        controller.likeFilm(film.getId(), user.getId());
        controller.likeFilm(film.getId(), user.getId());
        assertThat(controller.getFilm(film.getId()).getLikesByUsers()).containsExactly(user.getId());
        assertThat(controller.getFilm(film.getId()).getLikesCount()).isEqualTo(1);
    }

    @Test
    void shouldRemoveLikeFromFilm() {
        Film film = controller.createFilm(new Film(null, "Film", "Description",
                LocalDate.of(2000, 1, 1), 100));
        User user = createUser("user@mail.ru", "user");
        controller.likeFilm(film.getId(), user.getId());
        controller.dislikeFilm(film.getId(), user.getId());
        assertThat(controller.getFilm(film.getId()).getLikesByUsers()).isEmpty();
        assertThat(controller.getFilm(film.getId()).getLikesCount()).isZero();
    }

    @Test
    void shouldReturnPopularFilmsSortedByLikes() {
        Film firstFilm = controller.createFilm(new Film(null, "First", "Description",
                LocalDate.of(2000, 1, 1), 100));
        Film secondFilm = controller.createFilm(new Film(null, "Second", "Description",
                LocalDate.of(2001, 1, 1), 100));
        Film thirdFilm = controller.createFilm(new Film(null, "Third", "Description",
                LocalDate.of(2002, 1, 1), 100));
        User firstUser = createUser("first@mail.ru", "first");
        User secondUser = createUser("second@mail.ru", "second");
        controller.likeFilm(secondFilm.getId(), firstUser.getId());
        controller.likeFilm(secondFilm.getId(), secondUser.getId());
        controller.likeFilm(firstFilm.getId(), firstUser.getId());
        assertThat(controller.getPopularFilms(2)).containsExactly(secondFilm, firstFilm);
        assertThat(controller.getPopularFilms(10)).containsExactly(secondFilm, firstFilm, thirdFilm);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenLikeFilmDoesNotExist() {
        User user = createUser("user@mail.ru", "user");
        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> controller.likeFilm(1L, user.getId()));
    }

    @Test
    void shouldThrowNotFoundExceptionWhenLikeUserDoesNotExist() {
        Film film = controller.createFilm(new Film(null, "Film", "Description",
                LocalDate.of(2000, 1, 1), 100));
        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> controller.likeFilm(film.getId(), 1L));
    }

    private User createUser(String email, String login) {
        User user = new User(null, email, login, login, LocalDate.of(2000, 1, 1));
        user.setId(userStorage.getNextId());
        return userStorage.createUser(user);
    }
}
