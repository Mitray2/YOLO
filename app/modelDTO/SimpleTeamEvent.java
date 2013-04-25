package modelDTO;

import models.TeamMemberActivity;

/**
 * TODO class javadoc
 */
public class SimpleTeamEvent {

    public Long teamId;
    public String teamName;

    public SimpleUser user;

    public Long topicId;
    public String topicName;
    public boolean isMainTopic;
    public boolean isPublicTopic;

    public int action;
    public Long time;

    public SimpleTopicMessage message;

    public SimpleTeamEvent(Long teamId, String teamName, SimpleUser user, Long topicId, String topicName, boolean main, boolean aPublic, int action, Long time, SimpleTopicMessage message) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.user = user;
        this.topicId = topicId;
        this.topicName = topicName;
        isMainTopic = main;
        isPublicTopic = aPublic;
        this.action = action;
        this.time = time;
        this.message = message;
    }

    public static SimpleTeamEvent fromTeamMemberActivity(TeamMemberActivity activity) {
        return new SimpleTeamEvent(activity.team.id, activity.team.name, SimpleUser.fromFullUser(activity.user),
                activity.topic != null ? activity.topic.id : null,
                activity.topic != null ? activity.topic.name : activity.topicName,
                activity.topic != null && activity.topic.mainTopic,
                activity.topic != null && activity.topic.publicTopic,
                activity.action,
                activity.actionDate.getTime(),
                activity.topic != null ? SimpleTopicMessage.fromFullMessage(activity.message) : null);
    }
}
