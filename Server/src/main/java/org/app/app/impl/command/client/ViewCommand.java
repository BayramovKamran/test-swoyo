package org.app.app.impl.command.client;

import io.netty.channel.ChannelHandlerContext;
import org.app.adapter.repository.TopicRepository;
import org.app.adapter.repository.VoteRepository;
import org.app.adapter.rest.ServerHandler;
import org.app.app.api.Command;
import org.app.domain.Topic.Topic;
import org.app.domain.Vote.Vote;

import java.util.List;
import java.util.Map;

public class ViewCommand implements Command {
    @Override
    public void execute(ChannelHandlerContext ctx, Map<String, String> params, ServerHandler handler) {
        if (handler.getClient() == null) {
            ctx.writeAndFlush("Сначала выполните login");
            return;
        }

        String topicName = params.get("t");
        String voteName = params.get("v");
        StringBuilder response = new StringBuilder();

        if (topicName != null) {
            Topic topic = TopicRepository.getInstance().getTopic(topicName);
            if (topic == null) {
                ctx.writeAndFlush("Топик '" + topicName + "' не найден.");
                return;
            }
            if (voteName != null) {
                Vote vote = VoteRepository.getInstance().getVote(voteName, topic);
                if (vote == null) {
                    ctx.writeAndFlush("Голосование '" + voteName + "' в топике '" + topicName + "' не найдено.");
                    return;
                }
                response.append("Информация о голосовании:\n");
                response.append("Название: ").append(vote.getName()).append("\n");
                response.append("Описание: ").append(vote.getDescription()).append("\n");
                response.append("Варианты и количество голосов:\n");
                Map<String, Integer> options = vote.getOptions();
                if (options == null || options.isEmpty()) {
                    response.append("Нет вариантов для данного голосования.\n");
                } else {
                    for (Map.Entry<String, Integer> entry : options.entrySet()) {
                        response.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
                    }
                }
            } else {
                List<Vote> votes = VoteRepository.getInstance().getVotesByTopic(topic);
                if (votes.isEmpty()) {
                    response.append("В топике '").append(topic.getName()).append("' нет голосований.");
                } else {
                    response.append("Голосования в топике '").append(topic.getName()).append("':\n");
                    for (Vote vote : votes) {
                        response.append("- ").append(vote.getName()).append("\n");
                    }
                }
            }
        } else {
            List<Topic> topics = TopicRepository.getInstance().getAllTopics();
            if (topics.isEmpty()) {
                response.append("Нет созданных топиков.");
            } else {
                response.append("Список топиков:\n");
                for (Topic topic : topics) {
                    List<Vote> votes = VoteRepository.getInstance().getVotesByTopic(topic);
                    response.append("- ").append(topic.getName())
                            .append(" (голосований: ").append(votes.size()).append(")\n");
                }
            }
        }
        ctx.writeAndFlush(response.toString());
    }
}
