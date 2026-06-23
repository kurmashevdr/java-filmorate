package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ErrorCode;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import java.time.LocalDate;
import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private static final LocalDate EARLIEST_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private static final int MAX_DESCRIPTION_LENGTH = 200;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Collection<Film> getFilms() {
        log.info("Requested all films. Total films: {}", filmStorage.getFilmCount());
        return filmStorage.getFilms();
    }

    public Film getFilm(Long id) {
        Film film = filmStorage.getFilm(id);
        if (film == null) {
            throw new NotFoundException("Film not found");
        }
        log.info("Requested film with id={}", id);
        return film;
    }

    public Film createFilm(Film film) {
        validateFilm(film);
        filmStorage.createFilm(film);
        log.info("Created film with id={}", film.getId());
        return film;
    }

    public Film updateFilm(Film film) {
        if (film.getId() == null) {
            throw new ValidationException(ErrorCode.EMPTY_FILM_ID, "Film id cannot be null");
        }
        if (!filmStorage.isFilmExists(film.getId())) {
            throw new NotFoundException("Film not found");
        }
        validateFilm(film);
        filmStorage.updateFilm(film);
        log.info("Updated film with id={}", film.getId());
        return film;
    }

    public void likeFilm(Long filmId, Long userId) {
        Film film = filmStorage.getFilm(filmId);
        User user = userStorage.getUser(userId);
        if (film == null) {
            throw new NotFoundException("Film not found");
        }
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        if (filmStorage.isFilmLikedByUser(filmId, userId)) {
            log.warn("Film already liked by user with id={}", userId);
            return;
        }
        filmStorage.likeFilm(filmId, userId);
    }

    public void dislikeFilm(Long filmId, Long userId) {
        Film film = filmStorage.getFilm(filmId);
        User user = userStorage.getUser(userId);
        if (film == null) {
            throw new NotFoundException("Film not found");
        }
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        if (!filmStorage.isFilmLikedByUser(filmId, userId)) {
            log.warn("Film is not liked by user with id={}", userId);
            return;
        }
        filmStorage.dislikeFilm(filmId, userId);
    }

    public Collection<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }

    private void validateFilm(Film film) {
        if (film == null) {
            log.warn("Film validation failed: request body is empty");
            throw new ValidationException(ErrorCode.EMPTY_FILM, "Film cannot be empty");
        }
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Film validation failed: name is empty");
            throw new ValidationException(ErrorCode.EMPTY_FILM_NAME, "Film name cannot be empty");
        }
        if (film.getDescription() != null && film.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
            log.warn("Film validation failed: description is longer than 200 characters");
            throw new ValidationException(ErrorCode.TOO_LONG_FILM_DESCRIPTION, "Maximum length of 200 characters");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(EARLIEST_RELEASE_DATE)) {
            log.warn("Film validation failed: release date is before {}", EARLIEST_RELEASE_DATE);
            throw new ValidationException(ErrorCode.INVALID_FILM_RELEASE_DATE, "Film release date cannot be before " +
                    "28.12.1895");
        }
        if (film.getDuration() <= 0) {
            log.warn("Film validation failed: duration is not positive");
            throw new ValidationException(ErrorCode.NON_POSITIVE_FILM_DURATION, "Film duration must be positive");
        }
    }

}
