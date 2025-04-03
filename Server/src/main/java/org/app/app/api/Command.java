package org.app.app.api;

import io.netty.channel.ChannelHandlerContext;
import org.app.adapter.rest.ServerHandler;

import java.util.Map;

public interface Command {

    void execute(ChannelHandlerContext ctx, Map<String,String> params, ServerHandler handler);
}
