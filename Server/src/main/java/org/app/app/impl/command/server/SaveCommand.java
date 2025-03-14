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
import java.util.HashMap;
import java.util.Map;

public class SaveCommand implements ServerCommand {
    @Override
    public void execute(ChannelHandlerContext ctx, Map<String, String> params, ServerHandler handler) {
        String filename = params.get("filename");
        if (filename == null || filename.isEmpty()) {
            System.out.println("Ошибка: укажите имя файла. Пример: save data");
            return;
        }

        String resourcePath = System.getProperty("user.dir") + "/Server/src/main/resources";
        File resourceDir = new File(resourcePath);

        if (!resourceDir.exists()) {
            resourceDir.mkdirs();
        }

        File file = new File(resourceDir, filename+".json");
        System.out.println("Сохранение данных в файл: " + file.getAbsolutePath());

        Map<String, Object> data = new HashMap<>();
        data.put("users", UserRepository.getInstance().getAllUsers());
        data.put("topics", TopicRepository.getInstance().getAllTopics());
        data.put("votes", VoteRepository.getInstance().getAllVotes());

        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, data);
            System.out.println("Данные успешно сохранены в файл: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении данных: " + e.getMessage());
        }
    }
}
