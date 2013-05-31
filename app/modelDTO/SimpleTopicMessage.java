package modelDTO;

import models.TopicMessage;
import models.User;

import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;

/**
 * Simplified topic message model to use in views
 */
public class SimpleTopicMessage {

    public SimpleTopicMessage() {
    }

    public SimpleTopicMessage(Long id, Long topic, String text, Long time, SimpleUser from, List<Long> usersLiked) {
        this.id = id;
        this.topic = topic;
        this.text = text;
        this.time = time;
        this.from = from;
        this.from = from;
        this.usersLiked = usersLiked;
    }

    public Long id;
    public Long topic;
    public String text;
    public Long time;
    public SimpleUser from;
    public List<Long> usersLiked;

    public static SimpleTopicMessage fromFullMessage(TopicMessage msg) {
        return msg == null ? null : new SimpleTopicMessage(msg.id, msg.topic.id, msg.text, msg.createDate.getTime(),
                                                           SimpleUser.fromFullUser(msg.from),
                                                           extract(msg.usersLiked, on(User.class).id));
    }
}
