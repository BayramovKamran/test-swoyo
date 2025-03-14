package org.app.adapter.repository;

import org.app.domain.Topic.Topic;
import org.app.domain.User.User;
import org.app.domain.Vote.Vote;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VoteRepository {
    private static VoteRepository instance;
    private final Map<String, List<Vote>> votesByTopic = new HashMap<>();

    private VoteRepository() {}

    public static VoteRepository getInstance() {
        if (instance == null) {
            instance = new VoteRepository();
        }
        return instance;
    }

    public void addVote(Vote vote) {
        if (vote == null || vote.getTopic() == null || vote.getTopic().getName() == null) {
            return;
        }
        String topicName = vote.getTopic().getName().toLowerCase();
        votesByTopic.computeIfAbsent(topicName, k -> new ArrayList<>()).add(vote);
    }

    public List<Vote> getVotesByTopic(Topic topic) {
        if (topic == null || topic.getName() == null) {
            return new ArrayList<>();
        }
        String topicName = topic.getName().toLowerCase();
        return votesByTopic.getOrDefault(topicName, new ArrayList<>());
    }

    public Vote getVote(String voteName, Topic topic) {
        if (topic == null || voteName == null) {
            return null;
        }
        String topicName = topic.getName().toLowerCase();
        List<Vote> votes = votesByTopic.getOrDefault(topicName, new ArrayList<>());
        for (Vote vote : votes) {
            if (vote.getName().equalsIgnoreCase(voteName)) {
                return vote;
            }
        }
        return null;
    }

    public List<Vote> getAllVotes() {
        List<Vote> allVotes = new ArrayList<>();
        for (List<Vote> votes : votesByTopic.values()) {
            allVotes.addAll(votes);
        }
        return allVotes;
    }

    public boolean removeVote(Vote vote, Topic topic) {
        if (topic == null || vote == null) return false;
        String topicName = topic.getName().toLowerCase();
        List<Vote> votes = votesByTopic.get(topicName);
        if (votes != null) {
            return votes.remove(vote);
        }
        return false;
    }

    public void loadVotes(List<Map<String, Object>> data) {
        votesByTopic.clear();
        for (Map<String, Object> voteData : data) {
            String name = (String) voteData.get("name");
            String description = (String) voteData.get("description");

            Map<String, Object> topicData = (Map<String, Object>) voteData.get("topic");
            String topicName = (String) topicData.get("name");

            Map<String, Object> creatorData = (Map<String, Object>) voteData.get("creator");
            String creatorUsername = (String) creatorData.get("username");

            Topic topic = TopicRepository.getInstance().getAllTopics().stream()
                    .filter(t -> t.getName().equals(topicName))
                    .findFirst().orElse(null);

            User creator = UserRepository.getInstance().getAllUsers().stream()
                    .filter(u -> u.getUsername().equals(creatorUsername))
                    .findFirst().orElse(null);

            if (topic != null && creator != null) {
                Map<String, Integer> options = (Map<String, Integer>) voteData.get("options");
                Vote vote = new Vote(name, description, options, creator, topic);
                addVote(vote);
            }
        }
    }
    public void clear(){
        votesByTopic.clear();
    }
}
