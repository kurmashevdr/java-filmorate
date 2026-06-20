package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long currentMaxId = 0;

    @Override
    public User createUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUser(Long id) {
        return users.get(id);
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void deleteUser(Long id) {
        users.remove(id);
    }

    @Override
    public void addFriend(Long id, Long friendId) {
        users.get(id).addFriend(friendId);
        users.get(friendId).addFriend(id);
    }

    @Override
    public void removeFriend(Long id, Long friendId) {
        users.get(id).removeFriend(friendId);
        users.get(friendId).removeFriend(id);
    }

    @Override
    public Collection<User> getFriends(Long id) {
        return users.get(id).getFriends().stream()
                .map(users::get)
                .toList();
    }

    @Override
    public Collection<User> getCommonFriends(Long id, Long otherId) {
        User user = users.get(id);
        User otherUser = users.get(otherId);
        return user.getFriends().stream()
                .filter(friendId -> otherUser.getFriends().contains(friendId))
                .map(users::get)
                .toList();
    }

    @Override
    public Collection<User> getUsers() {
        return users.values();
    }

    @Override
    public Long getNextId() {
        return ++currentMaxId;
    }

    @Override
    public Integer getUserCount() {
        return users.size();
    }

    @Override
    public Boolean isUserExists(Long id) {
        return users.containsKey(id);
    }
}
