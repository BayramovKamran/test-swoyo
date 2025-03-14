package org.app.app.impl.command.client;

import io.netty.channel.ChannelHandlerContext;
import org.app.adapter.repository.TopicRepository;
import org.app.adapter.repository.VoteRepository;
import org.app.adapter.rest.ServerHandler;
import org.app.app.api.Command;
import org.app.domain.Topic.Topic;
import org.app.domain.Vote.Vote;

import java.util.Map;

public class VoteCommand implements Command {
    @Override
    public void execute(ChannelHandlerContext ctx, Map<String, String> params, ServerHandler handler) {
        if (handler.getClient() == null) {
            ctx.writeAndFlush("Сначала выполните login");
            return;
        }

        String topicName = params.get("t");
        String voteName = params.get("v");
        if (topicName == null || topicName.isEmpty() || voteName == null || voteName.isEmpty()) {
            ctx.writeAndFlush("Неверный формат vote. Пример: vote -t=MyTopic -v=VoteName");
            return;
        }

        Topic topic = TopicRepository.getInstance().getTopic(topicName);
        if (topic == null) {
            ctx.writeAndFlush(String.format("Топик '%s' не найден.", topicName));
            return;
        }

        Vote vote = VoteRepository.getInstance().getVote(voteName, topic);
        if (vote == null) {
            ctx.writeAndFlush(String.format("Голосование '%s' в топике '%s' не найдено.", voteName, topicName));
            return;
        }

        handler.setPendingVote(vote);
        StringBuilder response = new StringBuilder();
        response.append(String.format("Голосование '%s' в топике '%s':\n", voteName, topicName));
        response.append("Доступные варианты:\n");
        vote.getOptions().forEach((option, count) ->
                response.append(String.format("- %s (текущий результат: %d)\n", option, count))
        );
        response.append("Пожалуйста, введите название выбранного варианта для голосования:");
        ctx.writeAndFlush(response.toString());
    }
}
