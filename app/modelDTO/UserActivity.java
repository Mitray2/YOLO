package modelDTO;

import models.Message;
import play.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO class javadoc
 */
public class UserActivity {
    public TeamActivity teamActivity;
    public List<Message> unreadMessages = new ArrayList<Message>();

    public UserActivity() {
    }

    public UserActivity(List<Message> unreadMessages) {
        this.unreadMessages = unreadMessages;
    }

    public UserActivity(TeamActivity teamActivity, List<Message> unreadMessages) {
        this.teamActivity = teamActivity;
        this.unreadMessages = unreadMessages;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        int newMessages = unreadMessages.size();
        sb.append(newMessages > 0 ? newMessages : "no").append(" unread messages;\n")
          .append(teamActivity != null ? teamActivity.toString() : "no group activity");

        return sb.toString();
    }
}
