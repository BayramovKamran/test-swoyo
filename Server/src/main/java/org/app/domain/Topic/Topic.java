package org.app.domain.Topic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.app.domain.User.User;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Topic{
    private String name;
    private User owner;
}
