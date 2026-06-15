package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {"email", "login", "name", "birthday"})
public class User {
    private Long id;
    @NotBlank
    @Email
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    private Map<Long, User> friends = new HashMap<>();
    private Map<Long, Film> likedFilms = new HashMap<>();

    public User(Long id, String email, String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }

    public void addFriend(User friend) {
        friends.put(friend.getId(), friend);
    }

    public void removeFriend(User friend) {
        friends.remove(friend.getId());
    }

    public Collection<User> getFriends() {
        return friends.values();
    }

    public void likeFilm(Film film) {
        likedFilms.put(film.getId(), film);
    }

    public void unlikeFilm(Long filmId) {
        likedFilms.remove(filmId);
    }
}
