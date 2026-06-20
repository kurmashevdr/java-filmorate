package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private long currentMaxId = 0;
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Long getNextId() {
        return ++currentMaxId;
    }

    @Override
    public Film getFilm(Long id) {
        return films.get(id);
    }

    @Override
    public Film createFilm(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public void deleteFilm(Long id) {
        films.remove(id);
    }

    @Override
    public Collection<Film> getFilms() {
        return films.values();
    }

    @Override
    public Collection<Film> getPopularFilms(Integer count) {
        return films.values().stream()
                .sorted((f1, f2) -> f2.getLikesCount() - f1.getLikesCount())
                .limit(count)
                .toList();
    }

    @Override
    public Integer getFilmCount() {
        return films.size();
    }

    @Override
    public Boolean isFilmExists(Long id) {
        return films.containsKey(id);
    }

    @Override
    public void likeFilm(Long filmId, Long userId) {
        films.get(filmId).addLike(userId);
    }

    @Override
    public void dislikeFilm(Long filmId, Long userId) {
        films.get(filmId).removeLike(userId);
    }

    @Override
    public Boolean isFilmLikedByUser(Long filmId, Long userId) {
        return films.get(filmId).getLikesByUsers().contains(userId);
    }
}
