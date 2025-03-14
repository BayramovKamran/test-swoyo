package org.app.app.impl.command.client;

import io.netty.channel.ChannelHandlerContext;
import org.app.adapter.repository.TopicRepository;
import org.app.adapter.rest.ServerHandler;
import org.app.app.api.Command;
import org.app.domain.Topic.Topic;

import java.util.Map;

public class CreateTopicCommand implements Command {
    @Override
    public void execute(ChannelHandlerContext ctx, Map<String, String> params, ServerHandler handler) {
        if (handler.getClient() == null) {
            ctx.writeAndFlush("Сначала выполните login");
            return;
        }

        String topicName = params.get("n");
        if (topicName == null || topicName.isEmpty()) {
            ctx.writeAndFlush("Неверный формат create topic. Пример: create topic -n=forest");
            return;
        }

        Topic topic = TopicRepository.getInstance().getTopic(topicName);

        if (topic != null){
            ctx.writeAndFlush(String.format("Тема с под названием %s уже существует", topicName));
            return;
        }

        Topic newTopic = new Topic();
        newTopic.setName(topicName);
        newTopic.setOwner(handler.getClient());
        TopicRepository.getInstance().addTopic(newTopic);

        ctx.writeAndFlush(String.format("Тема '%s' успешно создана", topicName));
    }
}
