package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ErrorCode;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private static final LocalDate EARLIEST_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private static final int MAX_DESCRIPTION_LENGTH = 200;
    private long currentMaxId = 0;
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getFilms() {
        log.info("Requested all films. Total films: {}", films.size());
        return films.values();
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) {
        validateFilm(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Created film with id={}", film.getId());
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        if (film.getId() == null) {
            throw new ValidationException(ErrorCode.EMPTY_FILM_ID, "Film id cannot be null");
        }
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("Film not found");
        }
        validateFilm(film);
        films.put(film.getId(), film);
        log.info("Updated film with id={}", film.getId());
        return film;
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
            throw new ValidationException(ErrorCode.INVALID_FILM_RELEASE_DATE, "Film release date cannot be before 28.12.1895");
        }
        if (film.getDuration() <= 0) {
            log.warn("Film validation failed: duration is not positive");
            throw new ValidationException(ErrorCode.NON_POSITIVE_FILM_DURATION, "Film duration must be positive");
        }
    }

    private long getNextId() {
        return ++currentMaxId;
    }
}
