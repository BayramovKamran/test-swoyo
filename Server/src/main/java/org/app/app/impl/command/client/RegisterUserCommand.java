package org.app.app.impl.command.client;

import io.netty.channel.ChannelHandlerContext;
import org.app.adapter.repository.UserRepository;
import org.app.adapter.rest.ServerHandler;
import org.app.app.api.Command;
import org.app.domain.User.User;

import java.util.Map;

public class RegisterUserCommand implements Command {
    @Override
    public void execute(ChannelHandlerContext ctx, Map<String, String> params, ServerHandler handler) {
        String username = params.get("r");
        if (username == null || username.isEmpty()) {
            ctx.writeAndFlush("Неверный формат register. Пример: register -r=username");
            return;
        }

        User user = UserRepository.getInstance().getUser(username);

        if (user != null){
            ctx.writeAndFlush(String.format("Пользователя с ником %s уже зарегистрирован",username));
            return;
        }

        UserRepository.getInstance().addUser(new User(username));
        ctx.writeAndFlush("Пользователь " + username + " успешно зарегистрирован");

    }
}
