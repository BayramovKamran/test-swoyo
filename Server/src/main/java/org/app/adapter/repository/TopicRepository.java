package org.app.adapter.repository;

import org.app.domain.Topic.Topic;
import org.app.domain.User.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public void loadTopics(List<Map<String, Object>> data) {
        topics.clear(); // Очищаем старые данные перед загрузкой новых
        for (Map<String, Object> topicData : data) {
            String name = (String) topicData.get("name");

            Map<String, Object> ownerData = (Map<String, Object>) topicData.get("owner");
            String ownerUsername = (String) ownerData.get("username");

            User owner = UserRepository.getInstance().getAllUsers().stream()
                    .filter(u -> u.getUsername().equals(ownerUsername))
                    .findFirst().orElse(null);

            Topic topic = new Topic(name, owner);
            topics.add(topic);
        }
    }

    public void clear(){
        topics.clear();
    }

}
