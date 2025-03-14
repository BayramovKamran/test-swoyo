package org.app.adapter.rest.server;

import org.app.app.api.ServerCommand;
import org.app.app.impl.command.server.ExitCommand;
import org.app.app.impl.command.server.LoadCommand;
import org.app.app.impl.command.server.SaveCommand;

import java.util.HashMap;
import java.util.Map;

public class ServerCommandRegistry {
    private static ServerCommandRegistry instance;
    private final Map<String, ServerCommand> commands = new HashMap<>();

    private ServerCommandRegistry() {
        commands.put("load", new LoadCommand());
        commands.put("save", new SaveCommand());
        commands.put("exit", new ExitCommand());
    }

    public static synchronized ServerCommandRegistry getInstance() {
        if (instance == null) {
            instance = new ServerCommandRegistry();
        }
        return instance;
    }

    public ServerCommand getCommand(String commandName) {
        if (commandName == null) return null;
        return commands.get(commandName.toLowerCase());
    }
}
