package org.app.app.impl.command.client;

import io.netty.channel.ChannelHandlerContext;
import org.app.adapter.repository.UserRepository;
import org.app.adapter.rest.ServerHandler;
import org.app.app.api.Command;
import org.app.domain.User.User;

import java.util.Map;

public class LoginCommand implements Command {
    @Override
    public void execute(ChannelHandlerContext ctx, Map<String,String> params, ServerHandler handler) {
        String username = params.get("u");
        if (username == null || username.isEmpty()) {
            ctx.writeAndFlush("Неверный формат login. Пример: login -u=username");
            return;
        }

        User user = UserRepository.getInstance().getUser(username);

        if (user == null){
            ctx.writeAndFlush(String.format("Пользователя с ником %s не существует",username));
            return;
        }

        handler.setClient(user);
        ctx.writeAndFlush("Пользователь " + username + " успешно авторизован");
    }
}
