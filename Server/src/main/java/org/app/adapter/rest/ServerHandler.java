package org.app.adapter.rest;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.app.adapter.rest.dto.CommandParser;
import org.app.adapter.rest.dto.CommandRequest;
import org.app.domain.User.User;
import org.app.domain.Vote.Vote;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Getter
@Setter
public class ServerHandler extends SimpleChannelInboundHandler<String> {
    private static final List<Channel> channels = new ArrayList<>();
    private User client;
    private Vote pendingVote;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("User connected:{}", ctx);
        channels.add(ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        if (client != null) {
            log.info("Get command from User {}, command {}", client.getUsername(), msg);
        }
        else{
            log.info("Get command {}, non user", msg);
        }

        if (pendingVote != null) {
            String optionInput = msg.trim();
            Map<String, Integer> options = pendingVote.getOptions();
            if (!options.containsKey(optionInput)) {
                ctx.writeAndFlush(String.format("Вариант '%s' не найден в голосовании '%s'.", optionInput, pendingVote.getName()));
            } else {
                options.compute(optionInput, (k, currentCount) -> currentCount + 1);
                ctx.writeAndFlush(String.format("Ваш голос за вариант '%s' учтен в голосовании '%s'.", optionInput, pendingVote.getName()));
            }
            pendingVote = null;
            return;
        }

        CommandRequest request = CommandParser.parse(msg);

        if (request == null) {
            ctx.writeAndFlush("Ошибка: пустая команда");
            return;
        }

        new CommandDispatcher().dispatch(ctx, request, this);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("User disconnect");
        ctx.close();
    }
}
