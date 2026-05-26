package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
        assertThatThrownBy(() -> controller.createFilm(film))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Film name cannot be empty");
    }

    @Test
    void shouldRejectTooLongDescription() {
        Film film = new Film(null, "Film", "a".repeat(201), LocalDate.of(2000, 1, 1), 100);
        assertThatThrownBy(() -> controller.createFilm(film))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Maximum length of 200 characters");
    }

    @Test
    void shouldRejectReleaseDateBeforeCinemaBirthday() {
        Film film = new Film(null, "Film", "Description", LocalDate.of(1895, 12, 27), 100);
        assertThatThrownBy(() -> controller.createFilm(film))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Film release date cannot be before 28.12.1895");
    }

    @Test
    void shouldRejectNonPositiveDuration() {
        Film film = new Film(null, "Film", "Description", LocalDate.of(2000, 1, 1), 0);
        assertThatThrownBy(() -> controller.createFilm(film))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Film duration must be positive");
    }

    @Test
    void shouldRejectEmptyFilm() {
        assertThatThrownBy(() -> controller.createFilm(null))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Film cannot be empty");
    }

    @Test
    void shouldRejectFilmWithEmptyFields() {
        Film film = new Film();

        assertThatThrownBy(() -> controller.createFilm(film))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Film name cannot be empty");
    }
}
