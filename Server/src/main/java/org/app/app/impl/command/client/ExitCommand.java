package org.app.app.impl.command.client;

import io.netty.channel.ChannelHandlerContext;
import org.app.adapter.rest.ServerHandler;
import org.app.app.api.Command;

import java.util.Map;

public class ExitCommand implements Command {

    @Override
    public void execute(ChannelHandlerContext ctx, Map<String, String> params, ServerHandler handler) {
        ctx.writeAndFlush("Завершаем работу программы...").addListener(future -> {
            if (ctx.channel().isActive()) {
                ctx.channel().close();
            }
        });
    }

}
