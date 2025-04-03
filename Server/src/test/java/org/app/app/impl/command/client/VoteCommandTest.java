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

public class VoteCommandTest {

    private VoteCommand command;
    private ChannelHandlerContext ctx;
    private ServerHandler handler;
    private Vote vote;

    @BeforeEach
    public void setUp() {
        command = new VoteCommand();
        ctx = Mockito.mock(ChannelHandlerContext.class);
        handler = Mockito.mock(ServerHandler.class);

        when(handler.getClient()).thenReturn(new User("user1"));

        Topic topic = new Topic();
        topic.setName("TestTopic");
        topic.setOwner(new User("user1"));
        TopicRepository.getInstance().addTopic(topic);

        vote = Vote.builder()
                .name("Vote1")
                .description("Описание")
                .options(new HashMap<>())
                .creator(new User("user1"))
                .topic(topic)
                .build();
        vote.getOptions().put("option1", 0);
        VoteRepository.getInstance().addVote(vote);
    }

    @Test
    public void testExecute_InvalidParameters() {
        Map<String, String> params = new HashMap<>();
        command.execute(ctx, params, handler);
        verify(ctx).writeAndFlush("Неверный формат vote. Пример: vote -t=MyTopic -v=VoteName");
    }

    @Test
    public void testExecute_TopicNotFound() {
        Map<String, String> params = new HashMap<>();
        params.put("t", "NonExistingTopic");
        params.put("v", "Vote1");
        command.execute(ctx, params, handler);
        verify(ctx).writeAndFlush("Топик 'NonExistingTopic' не найден.");
    }

    @Test
    public void testExecute_VoteNotFound() {
        Map<String, String> params = new HashMap<>();
        params.put("t", "TestTopic");
        params.put("v", "NonExistingVote");
        command.execute(ctx, params, handler);
        verify(ctx).writeAndFlush("Голосование 'NonExistingVote' в топике 'TestTopic' не найдено.");
    }

    @Test
    public void testExecute_Success() {
        Map<String, String> params = new HashMap<>();
        params.put("t", "TestTopic");
        params.put("v", "Vote1");
        command.execute(ctx, params, handler);

        ArgumentCaptor<Vote> voteCaptor = ArgumentCaptor.forClass(Vote.class);
        verify(handler).setPendingVote(voteCaptor.capture());
        Vote capturedVote = voteCaptor.getValue();

        assertEquals("Vote1", capturedVote.getName());
    }

}
