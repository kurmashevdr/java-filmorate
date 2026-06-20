package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;

public interface UserStorage {
    Long getNextId();
    Collection<User> getUsers();
    User getUser(Long id);
    User createUser(User user);
    User updateUser(User user);
    void deleteUser(Long id);
    void addFriend(Long id, Long friendId);
    void removeFriend(Long id, Long friendId);
    Collection<User> getFriends(Long id);
    Collection<User> getCommonFriends(Long id, Long otherId);
    Integer getUserCount();
    Boolean isUserExists(Long id);
}
