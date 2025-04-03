package org.app.adapter.rest.client.dto;

import java.util.HashMap;
import java.util.Map;

public class CommandParser {

    public static CommandRequest parse(String input) {
        if (input == null || input.trim().isEmpty()) {
            return null;
        }
        String[] tokens = input.trim().split("\\s+");
        if (tokens.length == 0) {
            return null;
        }
        
        String commandName = tokens[0].toLowerCase();
        int index = 1;
        if (commandName.equals("create") && tokens.length > 1) {
            if (tokens[1].equalsIgnoreCase("topic")) {
                commandName = "create topic";
                index = 2;
            } else if (tokens[1].equalsIgnoreCase("vote")) {
                commandName = "create vote";
                index = 2;
            }
        }

        Map<String, String> params = getStringStringMap(index, tokens);
        return new CommandRequest(commandName, params);
    }

    private static Map<String, String> getStringStringMap(int index, String[] tokens) {
        Map<String, String> params = new HashMap<>();
        for (int i = index; i < tokens.length; i++) {
            String token = tokens[i];
            if (token.startsWith("-") && token.contains("=")) {
                int eqIndex = token.indexOf('=');
                String key = token.substring(1, eqIndex);
                String value = token.substring(eqIndex + 1);
                params.put(key, value);
            } else {
                params.put("arg" + i, token);
            }
        }
        return params;
    }

}
