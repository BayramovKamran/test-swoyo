package org.app.adapter.rest.client;

import io.netty.channel.ChannelHandlerContext;
import org.app.adapter.rest.ServerHandler;
import org.app.adapter.rest.client.dto.CommandRequest;
import org.app.app.api.Command;
import org.app.app.impl.command.client.*;

import java.util.HashMap;
import java.util.Map;

public class CommandDispatcher {
    private final Map<String, Command> commandMap;

    public CommandDispatcher(){
        commandMap = new HashMap<>();
        commandMap.put("register", new RegisterUserCommand());
        commandMap.put("login", new LoginCommand());
        commandMap.put("create topic", new CreateTopicCommand());
        commandMap.put("create vote", new CreateVoteCommand());
        commandMap.put("delete", new DeleteCommand());
        commandMap.put("exit", new ExitCommand());
        commandMap.put("view", new ViewCommand());
        commandMap.put("vote", new VoteCommand());
        commandMap.put("info", new InfoCommand());
    }

    public void dispatch(ChannelHandlerContext ctx, CommandRequest request, ServerHandler handler) {
        Command command = commandMap.get(request.getCommandName());
        if (command != null) {
            command.execute(ctx, request.getParams(), handler);
        } else {
            ctx.writeAndFlush("Неизвестная команда: " + request.getCommandName());
        }
    }
}
