package modelDTO;

import models.Command;

/**
 * Container for latest activities in a group
 */
public class TeamActivity {
    public Command team;
    public int newTopics;
    public int newMessages;
    public int joinedMembers;
    public int leftMembers;

    public TeamActivity() {
    }

    public TeamActivity(Command team, int newTopics, int newMessages, int joinedMembers, int leftMembers) {
        this.team = team;
        this.newTopics = newTopics;
        this.newMessages = newMessages;
        this.joinedMembers = joinedMembers;
        this.leftMembers = leftMembers;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if(joinedMembers + leftMembers + newMessages + newTopics > 0){
            sb.append("new group activity:\n");
            if(newTopics > 0) sb.append(newTopics).append(" new topics\n");
            if(newMessages > 0) sb.append(newMessages).append(" new messages\n");
            if(leftMembers > 0) sb.append(leftMembers).append(" members left\n");
            if(joinedMembers > 0) sb.append(joinedMembers).append(" members joined\n");
        } else {
            sb.append("no group activity");
        }

        return sb.toString();
    }
}
