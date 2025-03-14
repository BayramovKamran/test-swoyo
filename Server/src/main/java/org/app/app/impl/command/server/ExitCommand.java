package org.app.app.impl.command.server;

import io.netty.channel.ChannelHandlerContext;
import org.app.adapter.rest.ServerHandler;
import org.app.app.api.ServerCommand;

import java.util.Map;

public class ExitCommand implements ServerCommand {
    @Override
    public void execute(ChannelHandlerContext ctx, Map<String, String> params, ServerHandler handler) {

    }
}
