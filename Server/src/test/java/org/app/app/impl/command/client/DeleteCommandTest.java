package org.app.app.impl.command.client;

import io.netty.channel.ChannelHandlerContext;
import org.app.adapter.repository.TopicRepository;
import org.app.adapter.repository.UserRepository;
import org.app.adapter.repository.VoteRepository;
import org.app.adapter.rest.ServerHandler;
import org.app.domain.Topic.Topic;
import org.app.domain.User.User;
import org.app.domain.Vote.Vote;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

public class DeleteCommandTest {
    private DeleteCommand command;
    private ChannelHandlerContext ctx;
    private ServerHandler handler;
    private Topic topic;
    private Vote vote;

    @BeforeEach
    public void setUp() {
        command = new DeleteCommand();
        ctx = Mockito.mock(ChannelHandlerContext.class);
        handler = Mockito.mock(ServerHandler.class);

        TopicRepository.getInstance().clear();

        topic = new Topic();
        topic.setName("TestTopic");
        topic.setOwner(new User("owner"));
        TopicRepository.getInstance().addTopic(topic);

        vote = Vote.builder()
                .name("Vote1")
                .description("Описание")
                .options(new HashMap<>())
                .creator(new User("owner"))
                .topic(topic)
                .build();
        vote.getOptions().put("option1", 0);
        VoteRepository.getInstance().addVote(vote);
    }

    @Test
    public void testExecute_NotLoggedIn() {
        when(handler.getClient()).thenReturn(null);
        Map<String, String> params = new HashMap<>();
        params.put("t", "TestTopic");
        params.put("v", "Vote1");

        command.execute(ctx, params, handler);
        verify(ctx).writeAndFlush("Сначала выполните login");
    }

    @Test
    public void testExecute_InvalidParameters() {
        when(handler.getClient()).thenReturn(new User("owner"));
        Map<String, String> params = new HashMap<>();
        command.execute(ctx, params, handler);
        verify(ctx).writeAndFlush("Неверный формат delete. Пример: delete -t=MyTopic -v=VoteName");
    }

    @Test
    public void testExecute_TopicNotFound() {
        when(handler.getClient()).thenReturn(new User("owner"));
        Map<String, String> params = new HashMap<>();
        params.put("t", "NonExistingTopic");
        params.put("v", "Vote1");
        command.execute(ctx, params, handler);
        verify(ctx).writeAndFlush("Топик 'NonExistingTopic' не найден.");
    }

    @Test
    public void testExecute_VoteNotFound() {
        when(handler.getClient()).thenReturn(new User("owner"));
        Map<String, String> params = new HashMap<>();
        params.put("t", "TestTopic");
        params.put("v", "NonExistingVote");
        command.execute(ctx, params, handler);
        verify(ctx).writeAndFlush("Голосование 'NonExistingVote' в топике 'TestTopic' не найдено.");
    }

    @Test
    public void testExecute_NotOwner() {
        when(handler.getClient()).thenReturn(new User("anotherUser"));
        Map<String, String> params = new HashMap<>();
        params.put("t", "TestTopic");
        params.put("v", "Vote1");
        command.execute(ctx, params, handler);
        verify(ctx).writeAndFlush("Удалять голосование может только его создатель");
    }

    @Test
    void testExecute_Success() {
        TopicRepository.getInstance().clear();
        VoteRepository.getInstance().clear();
        UserRepository.getInstance().clear();

        User creator = new User("TestUser");
        UserRepository.getInstance().addUser(creator);

        Topic topic = new Topic("TestTopic", creator);
        TopicRepository.getInstance().addTopic(topic);

        Vote vote = new Vote("Vote1", "Test vote", Map.of("Option1", 0), creator, topic);
        VoteRepository.getInstance().addVote(vote);

        ServerHandler handler = mock(ServerHandler.class);
        when(handler.getClient()).thenReturn(creator);

        DeleteCommand deleteCommand = new DeleteCommand();
        deleteCommand.execute(ctx, Map.of("t", "TestTopic", "v", "Vote1"), handler);

        verify(ctx).writeAndFlush("Голосование 'Vote1' успешно удалено из топика 'TestTopic'.");
    }
}
