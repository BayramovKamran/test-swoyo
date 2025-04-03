package org.app.adapter.repository;

import org.app.domain.Topic.Topic;
import org.app.domain.User.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TopicRepositoryTest {

    private TopicRepository repository;

    @BeforeEach
    void setUp() {
        repository = TopicRepository.getInstance();
        repository.clear();
    }

    @Test
    void testAddAndGetTopic() {
        User owner = new User("testUser");
        Topic topic = new Topic("TestTopic", owner);
        repository.addTopic(topic);

        Topic retrieved = repository.getTopic("TestTopic");
        assertNotNull(retrieved);
        assertEquals("TestTopic", retrieved.getName());
        assertEquals(owner, retrieved.getOwner());
    }

    @Test
    void testGetAllTopics() {
        repository.addTopic(new Topic("Topic1", new User("user1")));
        repository.addTopic(new Topic("Topic2", new User("user2")));

        List<Topic> topics = repository.getAllTopics();
        assertEquals(2, topics.size());
    }

    @Test
    void testGetNonExistingTopic() {
        assertNull(repository.getTopic("NonExisting"));
    }

}
