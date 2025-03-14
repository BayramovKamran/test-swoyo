package org.app.app.impl.command.client;

import io.netty.channel.ChannelHandlerContext;
import org.app.adapter.repository.TopicRepository;
import org.app.adapter.repository.VoteRepository;
import org.app.adapter.rest.ServerHandler;
import org.app.domain.Topic.Topic;
import org.app.domain.User.User;
import org.app.domain.Vote.Vote;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CreateVoteCommandTest {

    private CreateVoteCommand command;
    private ChannelHandlerContext ctx;
    private ServerHandler handler;
    private Topic topic;

    @BeforeEach
    public void setUp() {
        command = new CreateVoteCommand();
        ctx = Mockito.mock(ChannelHandlerContext.class);
        handler = Mockito.mock(ServerHandler.class);
        TopicRepository.getInstance().clear();
        VoteRepository.getInstance().clear();

        topic = new Topic();
        topic.setName("TestTopic");
        topic.setOwner(new User("user1"));
        TopicRepository.getInstance().addTopic(topic);

        when(handler.getClient()).thenReturn(new User("user1"));
    }

    @Test
    public void testExecute_InvalidTopicParameter() {
        Map<String, String> params = new HashMap<>();
        command.execute(ctx, params, handler);
        verify(ctx).writeAndFlush("Неверный формат create vote. Пример: create vote -t=<topic>");
    }

    @Test
    public void testExecute_TopicNotFound() {
        Map<String, String> params = new HashMap<>();
        params.put("t", "NonExistingTopic");
        command.execute(ctx, params, handler);
        verify(ctx).writeAndFlush("Тема с под названием 'NonExistingTopic' не существует");
    }

    @Test
    public void testExecute_MissingVoteName() {
        Map<String, String> params = new HashMap<>();
        params.put("t", "TestTopic");
        command.execute(ctx, params, handler);
        verify(ctx).writeAndFlush("Неверный формат create vote. Отсутствует параметр название голосования.");
    }

    @Test
    public void testExecute_Success() {
        Map<String, String> params = new HashMap<>();
        params.put("t", "TestTopic");
        params.put("name", "Vote1");
        params.put("desc", "Описание голосования");
        params.put("options", "option1, option2, option3");

        command.execute(ctx, params, handler);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(ctx).writeAndFlush(captor.capture());
        String response = captor.getValue();
        assertEquals("Голосование 'Vote1' успешно создано в топике 'TestTopic'.", response);

        Vote vote = VoteRepository.getInstance().getVote("Vote1", topic);
        assertNotNull(vote);
        assertEquals("Vote1", vote.getName());
        assertEquals("Описание голосования", vote.getDescription());
        assertTrue(vote.getOptions().containsKey("option1"));
        assertTrue(vote.getOptions().containsKey("option2"));
        assertTrue(vote.getOptions().containsKey("option3"));
    }
}
