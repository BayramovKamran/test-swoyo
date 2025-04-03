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
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class ViewCommandTest {

    private ViewCommand command;
    private ChannelHandlerContext ctx;
    private ServerHandler handler;
    private User user;
    private Topic topic;
    private Vote vote;

    @BeforeEach
    public void setUp() {
        command = new ViewCommand();
        ctx = Mockito.mock(ChannelHandlerContext.class);
        handler = Mockito.mock(ServerHandler.class);

        UserRepository.getInstance().clear();
        TopicRepository.getInstance().clear();
        VoteRepository.getInstance().clear();

        user = new User("user1");
        UserRepository.getInstance().addUser(user);
        when(handler.getClient()).thenReturn(user);
        topic = new Topic();
        topic.setName("TestTopic");
        topic.setOwner(user);
        TopicRepository.getInstance().addTopic(topic);

        Map<String, Integer> options = new HashMap<>();
        options.put("option1", 5);
        options.put("option2", 3);
        vote = Vote.builder()
                .name("Vote1")
                .description("Описание голосования")
                .options(options)
                .creator(user)
                .topic(topic)
                .build();
        VoteRepository.getInstance().addVote(vote);
    }

    @Test
    public void testExecute_ViewAllTopics() {
        Map<String, String> params = new HashMap<>();
        command.execute(ctx, params, handler);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(ctx).writeAndFlush(captor.capture());
        String response = captor.getValue();
        assertTrue(response.contains("Список топиков:"), "Response should list topics");
        assertTrue(response.contains("TestTopic"), "Response should contain 'TestTopic'");
    }

    @Test
    public void testExecute_ViewVotesInTopic() {
        Map<String, String> params = new HashMap<>();
        params.put("t", "TestTopic");
        command.execute(ctx, params, handler);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(ctx).writeAndFlush(captor.capture());
        String response = captor.getValue();
        assertTrue(response.contains("Голосования в топике 'TestTopic':"), "Response should list votes in topic");
        assertTrue(response.contains("Vote1"), "Response should contain vote 'Vote1'");
    }

    @Test
    public void testExecute_ViewVoteDetails() {
        Map<String, String> params = new HashMap<>();
        params.put("t", "TestTopic");
        params.put("v", "Vote1");
        command.execute(ctx, params, handler);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(ctx).writeAndFlush(captor.capture());
        String response = captor.getValue();
        assertTrue(response.contains("Информация о голосовании:"), "Response should contain header info");
        assertTrue(response.contains("Название: Vote1"), "Response should contain vote name");
        assertTrue(response.contains("Описание: Описание голосования"), "Response should contain vote description");
    }

}
