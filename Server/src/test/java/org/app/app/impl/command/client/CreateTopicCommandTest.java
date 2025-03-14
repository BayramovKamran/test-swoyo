package org.app.app.impl.command.client;

import io.netty.channel.ChannelHandlerContext;
import org.app.adapter.repository.TopicRepository;
import org.app.adapter.rest.ServerHandler;
import org.app.domain.User.User;
import org.app.domain.Topic.Topic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CreateTopicCommandTest {

    private CreateTopicCommand command;
    private ChannelHandlerContext ctx;
    private ServerHandler handler;

    @BeforeEach
    public void setUp() {
        command = new CreateTopicCommand();
        ctx = Mockito.mock(ChannelHandlerContext.class);
        handler = Mockito.mock(ServerHandler.class);
        TopicRepository.getInstance().clear();
    }

    @Test
    public void testExecute_NotLoggedIn() {
        when(handler.getClient()).thenReturn(null);
        Map<String, String> params = new HashMap<>();
        params.put("n", "TestTopic");

        command.execute(ctx, params, handler);

        verify(ctx).writeAndFlush("Сначала выполните login");
    }

    @Test
    public void testExecute_InvalidTopicName() {
        when(handler.getClient()).thenReturn(new User("user1"));
        Map<String, String> params = new HashMap<>();
        params.put("n", "");

        command.execute(ctx, params, handler);
        verify(ctx).writeAndFlush("Неверный формат create topic. Пример: create topic -n=forest");
    }

    @Test
    public void testExecute_TopicAlreadyExists() {
        when(handler.getClient()).thenReturn(new User("user1"));
        // Создаем тему заранее
        Topic topic = new Topic();
        topic.setName("ExistingTopic");
        topic.setOwner(new User("user1"));
        TopicRepository.getInstance().addTopic(topic);

        Map<String, String> params = new HashMap<>();
        params.put("n", "ExistingTopic");

        command.execute(ctx, params, handler);
        verify(ctx).writeAndFlush("Тема с под названием ExistingTopic уже существует");
    }

    @Test
    public void testExecute_Success() {
        when(handler.getClient()).thenReturn(new User("user1"));
        Map<String, String> params = new HashMap<>();
        params.put("n", "NewTopic");

        command.execute(ctx, params, handler);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(ctx).writeAndFlush(captor.capture());
        String response = captor.getValue();
        assertEquals("Тема 'NewTopic' успешно создана", response);

        Topic created = TopicRepository.getInstance().getTopic("NewTopic");
        assertNotNull(created);
        assertEquals("NewTopic", created.getName());
    }
}
