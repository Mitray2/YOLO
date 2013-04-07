package modelDTO;

import models.Message;
import models.TopicMessage;

/**
 * Simplified topic message model to use in views
 */
public class SimpleTopicMessage {

    public SimpleTopicMessage() {
    }

    public SimpleTopicMessage(Long id, Long topic, String text, Long time, SimpleUser from) {
        this.id = id;
        this.topic = topic;
        this.text = text;
        this.time = time;
        this.from = from;
    }

    public Long id;
    public Long topic;
    public String text;
    public Long time;
    public SimpleUser from;

    public static SimpleTopicMessage fromFullMessage(TopicMessage msg) {
        return new SimpleTopicMessage(msg.id, msg.topic.id, msg.text, msg.createDate.getTime(), SimpleUser.fromFullUser(msg.from));
    }
}
