package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ErrorCode;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class FilmControllerTest {
    private FilmController controller;

    @BeforeEach
    void setUp() {
        controller = new FilmController();
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
    void shouldRejectEmptyName() {
        Film film = new Film(null, " ", "Description", LocalDate.of(2000, 1, 1), 100);
        assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> controller.createFilm(film))
                .satisfies(exception -> assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.EMPTY_FILM_NAME));
    }

    @Test
    void shouldRejectTooLongDescription() {
        Film film = new Film(null, "Film", "a".repeat(201), LocalDate.of(2000, 1, 1), 100);
        assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> controller.createFilm(film))
                .satisfies(exception -> assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.TOO_LONG_FILM_DESCRIPTION));
    }

    @Test
    void shouldRejectReleaseDateBeforeCinemaBirthday() {
        Film film = new Film(null, "Film", "Description", LocalDate.of(1895, 12, 27), 100);
        assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> controller.createFilm(film))
                .satisfies(exception -> assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_FILM_RELEASE_DATE));
    }

    @Test
    void shouldRejectNonPositiveDuration() {
        Film film = new Film(null, "Film", "Description", LocalDate.of(2000, 1, 1), 0);
        assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> controller.createFilm(film))
                .satisfies(exception -> assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NON_POSITIVE_FILM_DURATION));
    }

    @Test
    void shouldRejectEmptyFilm() {
        assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> controller.createFilm(null))
                .satisfies(exception -> assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.EMPTY_FILM));
    }

    @Test
    void shouldRejectFilmWithEmptyFields() {
        Film film = new Film();

        assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> controller.createFilm(film))
                .satisfies(exception -> assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.EMPTY_FILM_NAME));
    }
}
