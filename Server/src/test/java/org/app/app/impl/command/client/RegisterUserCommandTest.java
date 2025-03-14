package org.app.app.impl.command.client;

import io.netty.channel.ChannelHandlerContext;
import org.app.adapter.repository.UserRepository;
import org.app.adapter.rest.ServerHandler;
import org.app.domain.User.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RegisterUserCommandTest {

    private RegisterUserCommand command;
    private ChannelHandlerContext ctx;
    private ServerHandler handler;

    @BeforeEach
    public void setUp() {
        command = new RegisterUserCommand();
        ctx = Mockito.mock(ChannelHandlerContext.class);
        handler = Mockito.mock(ServerHandler.class);
        UserRepository.getInstance().clear();
    }

    @Test
    public void testExecute_InvalidFormat() {
        Map<String, String> params = new HashMap<>();
        command.execute(ctx, params, handler);
        verify(ctx).writeAndFlush("Неверный формат register. Пример: register -r=username");
    }

    @Test
    public void testExecute_UserAlreadyExists() {
        Map<String, String> params = new HashMap<>();
        params.put("r", "user1");
        UserRepository.getInstance().addUser(new User("user1"));

        command.execute(ctx, params, handler);
        verify(ctx).writeAndFlush("Пользователя с ником user1 уже зарегистрирован");
    }

    @Test
    public void testExecute_Success() {
        Map<String, String> params = new HashMap<>();
        params.put("r", "user1");

        command.execute(ctx, params, handler);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(ctx).writeAndFlush(captor.capture());
        String response = captor.getValue();
        assertEquals("Пользователь user1 успешно зарегистрирован", response);

        User user = UserRepository.getInstance().getUser("user1");
        assertNotNull(user);
    }
}
