package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.Collection;

public interface FilmStorage {
    Long getNextId();
    Collection<Film> getFilms();
    Film getFilm(Long id);
    Film createFilm(Film film);
    Film updateFilm(Film film);
    void deleteFilm(Long id);
    Collection<Film> getPopularFilms(Integer count);
    Integer getFilmCount();
    Boolean isFilmExists(Long id);
    Boolean isFilmLikedByUser(Long filmId, Long userId);
    void likeFilm(Long filmId, Long userId);
    void dislikeFilm(Long filmId, Long userId);
}
