package org.app.adapter.repository;

import org.app.domain.User.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class UserRepositoryTest {
    private UserRepository repository;

    @BeforeEach
    void setUp() {
        repository = UserRepository.getInstance();
        repository.clear();
    }

    @Test
    void testAddAndGetUser() {
        User user = new User("testUser");
        repository.addUser(user);

        User retrieved = repository.getUser("testUser");
        assertNotNull(retrieved);
        assertEquals("testUser", retrieved.getUsername());
    }

    @Test
    void testGetAllUsers() {
        repository.addUser(new User("user1"));
        repository.addUser(new User("user2"));

        List<User> users = repository.getAllUsers();
        assertEquals(2, users.size());
    }

    @Test
    void testGetNonExistingUser() {
        assertNull(repository.getUser("NonExisting"));
    }
}
