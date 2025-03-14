package org.app.adapter.repository;

import org.app.domain.Topic.Topic;

import java.util.ArrayList;
import java.util.List;

public class TopicRepository {

    private static TopicRepository instance;
    private final List<Topic> topics = new ArrayList<>();

    private TopicRepository() {}

    public static TopicRepository getInstance() {
        if(instance == null) {
            instance = new TopicRepository();
        }
        return instance;
    }

    public void addTopic(Topic topic) {
        topics.add(topic);
    }

    public Topic getTopic(String name) {
        return topics.stream()
                .filter(t -> t.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public List<Topic> getAllTopics() {
        return topics;
    }
}
