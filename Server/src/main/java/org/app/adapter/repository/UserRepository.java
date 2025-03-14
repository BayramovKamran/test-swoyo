package org.app.adapter.repository;

import org.app.domain.User.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserRepository {
    private static UserRepository instance;
    private final List<User> users = new ArrayList<>();

    private UserRepository() {}

    public static UserRepository getInstance(){
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

    public void addUser(User user) {
        users.add(user);
    }

    public User getUser(String username) {
        return users.stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .findFirst()
                .orElse(null);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    public void loadUsers(List<Map<String, Object>> data) {
        users.clear();
        for (Map<String, Object> userData : data) {
            User user = new User((String) userData.get("username"));
            users.add(user);
        }
    }

    public void clear(){
        users.clear();
    }
}
