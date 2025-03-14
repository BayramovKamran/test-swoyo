package org.app.domain.Vote;

import lombok.*;
import org.app.domain.Topic.Topic;
import org.app.domain.User.User;

import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Vote{
    private String name;
    private String description;
    private Map<String, Integer> options;
    private User creator;
    private Topic topic;
}
