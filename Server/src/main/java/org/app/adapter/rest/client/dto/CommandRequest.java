package org.app.adapter.rest.client.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public class CommandRequest {
    private String commandName;
    private Map<String,String> params = new HashMap<>();
}
