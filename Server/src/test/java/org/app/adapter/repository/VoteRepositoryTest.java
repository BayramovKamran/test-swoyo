package org.app.adapter.repository;

import org.app.domain.Topic.Topic;
import org.app.domain.User.User;
import org.app.domain.Vote.Vote;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VoteRepositoryTest {
    private VoteRepository voteRepository;
    private Topic topic;
    private User user;

    @BeforeEach
    void setUp() {
        voteRepository = VoteRepository.getInstance();
        TopicRepository topicRepository = TopicRepository.getInstance();
        voteRepository.clear();
        topicRepository.clear();

        user = new User("testUser");
        topic = new Topic("TestTopic", user);
        topicRepository.addTopic(topic);
    }

    @Test
    void testAddAndGetVote() {
        Map<String, Integer> options = new HashMap<>();
        options.put("Option1", 0);
        options.put("Option2", 0);

        Vote vote = new Vote("Vote1", "Description", options, user, topic);
        voteRepository.addVote(vote);

        Vote retrieved = voteRepository.getVote("Vote1", topic);
        assertNotNull(retrieved);
        assertEquals("Vote1", retrieved.getName());
        assertEquals("Description", retrieved.getDescription());
    }

    @Test
    void testGetVotesByTopic() {
        voteRepository.addVote(new Vote("Vote1", "Description1", new HashMap<>(), user, topic));
        voteRepository.addVote(new Vote("Vote2", "Description2", new HashMap<>(), user, topic));

        List<Vote> votes = voteRepository.getVotesByTopic(topic);
        assertEquals(2, votes.size());
    }

    @Test
    void testRemoveVote() {
        Vote vote = new Vote("VoteToDelete", "Description", new HashMap<>(), user, topic);
        voteRepository.addVote(vote);

        boolean removed = voteRepository.removeVote(vote, topic);
        assertTrue(removed);
        assertNull(voteRepository.getVote("VoteToDelete", topic));
    }
}
