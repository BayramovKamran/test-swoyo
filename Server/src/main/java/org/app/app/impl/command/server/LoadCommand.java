package org.app.app.impl.command.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import org.app.adapter.repository.TopicRepository;
import org.app.adapter.repository.UserRepository;
import org.app.adapter.repository.VoteRepository;
import org.app.adapter.rest.ServerHandler;
import org.app.app.api.ServerCommand;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class LoadCommand implements ServerCommand {
    @Override
    public void execute(ChannelHandlerContext ctx, Map<String, String> params, ServerHandler handler) {
        String filename = params.get("filename");
        if (filename == null || filename.isEmpty()) {
            System.out.println("Ошибка: укажите имя файла. Пример: load data");
            return;
        }

        String resourcePath = System.getProperty("user.dir") + "/Server/src/main/resources";
        File file = new File(resourcePath, filename + ".json");

        if (!file.exists()) {
            System.out.println("Ошибка: файл " + file.getAbsolutePath() + " не найден.");
            return;
        }

        System.out.println("Загрузка данных из файла: " + file.getAbsolutePath());

        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String, Object> data = mapper.readValue(file, Map.class);

            List<Map<String, Object>> users = (List<Map<String, Object>>) data.get("users");
            List<Map<String, Object>> topics = (List<Map<String, Object>>) data.get("topics");
            List<Map<String, Object>> votes = (List<Map<String, Object>>) data.get("votes");

            if (users != null) {
                UserRepository.getInstance().loadUsers(users);
            }
            if (topics != null) {
                TopicRepository.getInstance().loadTopics(topics);
            }
            if (votes != null) {
                VoteRepository.getInstance().loadVotes(votes);
            }

            System.out.println("Данные успешно загружены из " + filename);
        } catch (IOException e) {
            System.out.println("Ошибка при загрузке данных: " + e.getMessage());
        }
    }
}
