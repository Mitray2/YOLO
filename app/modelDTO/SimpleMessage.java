package modelDTO;

import models.Message;

import java.util.Date;

/**
 * TODO class javadoc
 */
public class SimpleMessage {

    public SimpleMessage(Long id, String text, Long time, SimpleUser from, SimpleUser to, Boolean read) {
        this.id = id;
        this.text = text;
        this.time = time;
        this.from = from;
        this.to = to;
        isRead = read;
    }

    public Long id;
    public String text;
    public Long time;
    public SimpleUser from;
    public SimpleUser to;
    public Boolean isRead;

    public static SimpleMessage fromFullMessage(Message msg) {
        return fromFullMessage(msg, true);
    }

    public static SimpleMessage fromFullMessage(Message msg, boolean includeRecepient) {
        return new SimpleMessage(msg.id, msg.text, msg.time.getTime(), SimpleUser.fromFullUser(msg.from),
                includeRecepient ? SimpleUser.fromFullUser(msg.to) : null, msg.isRead );
    }
}
