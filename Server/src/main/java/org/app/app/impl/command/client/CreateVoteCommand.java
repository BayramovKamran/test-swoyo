package org.app.app.impl.command.client;

import io.netty.channel.ChannelHandlerContext;
import org.app.adapter.repository.TopicRepository;
import org.app.adapter.repository.VoteRepository;
import org.app.adapter.rest.ServerHandler;
import org.app.app.api.Command;
import org.app.domain.Topic.Topic;
import org.app.domain.Vote.Vote;

import java.util.HashMap;
import java.util.Map;

public class CreateVoteCommand implements Command {

    @Override
    public void execute(ChannelHandlerContext ctx, Map<String, String> params, ServerHandler handler) {
        if (handler.getClient() == null) {
            ctx.writeAndFlush("Сначала выполните login");
            return;
        }

        String topicName = params.get("t");
        if (topicName == null || topicName.isEmpty()) {
            ctx.writeAndFlush("Неверный формат create vote. Пример: create vote -t=<topic>");
            return;
        }

        Topic topic = TopicRepository.getInstance().getTopic(topicName);
        if (topic == null){
            ctx.writeAndFlush(String.format("Тема с под названием '%s' не существует", topicName));
            return;
        }

        String voteName = params.get("name");
        if (voteName == null || voteName.isEmpty()) {
            ctx.writeAndFlush("Неверный формат create vote. Отсутствует параметр название голосования.");
            return;
        }

        if (VoteRepository.getInstance().getVote(voteName, topic) != null) {
            ctx.writeAndFlush(String.format("Голосование '%s' уже существует в топике '%s'.", voteName, topicName));
            return;
        }

        String voteDesc = params.get("desc");
        if (voteDesc == null || voteDesc.isEmpty()) {
            ctx.writeAndFlush("Неверный формат create vote. Отсутствует параметр описание");
            return;
        }

        String optionsStr = params.get("options");
        if (optionsStr == null || optionsStr.isEmpty()) {
            ctx.writeAndFlush("Неверный формат create vote. Отсутствуют варианты");
            return;
        }

        String[] optionArray = optionsStr.split(",");
        Map<String, Integer> optionsMap = new HashMap<>();
        for (String option : optionArray) {
            option = option.trim();
            if (!option.isEmpty()) {
                optionsMap.put(option, 0);
            }
        }

        if (optionsMap.isEmpty()) {
            ctx.writeAndFlush("Неверный формат create vote. Голосование должно содержать хотя бы один вариант.");
            return;
        }

        Vote newVote = Vote.builder()
                .name(voteName)
                .description(voteDesc)
                .options(optionsMap)
                .creator(handler.getClient())
                .topic(topic)
                .build();

        VoteRepository.getInstance().addVote(newVote);

        ctx.writeAndFlush(String.format("Голосование '%s' успешно создано в топике '%s'.", voteName, topicName));
    }

}
