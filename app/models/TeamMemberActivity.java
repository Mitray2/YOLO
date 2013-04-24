package models;

import play.Logger;
import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
public class TeamMemberActivity extends Model {

    @ManyToOne
	public Command team;

    @ManyToOne
	public User user;
	public int action;
	public Date actionDate;

    @ManyToOne
    public Topic topic;
    public String topicName;

    @ManyToOne
    public TopicMessage message;

    public TeamMemberActivity() {
    }

    public TeamMemberActivity(Command team, User user, int action) {
        this.team = team;
        this.user = user;
        this.action = action;
        this.actionDate = new Date();
    }

    public TeamMemberActivity(Command team, User user, int action, Topic topic) {
        this.team = team;
        this.user = user;
        this.action = action;
        this.actionDate = new Date();
        this.topic = topic;
    }

    public TeamMemberActivity(Command team, User user, int action, String topicName) {
        this.team = team;
        this.user = user;
        this.action = action;
        this.actionDate = new Date();
        this.topicName = topicName;
    }

    public TeamMemberActivity(Command team, User user, int action, Topic topic, TopicMessage message) {
        this.team = team;
        this.user = user;
        this.action = action;
        this.actionDate = new Date();
        this.topic = topic;
        this.message = message;
    }

    @Override
    public String toString() {
        return "TeamMemberActivity{" +
                "team=" + team +
                ", user=" + user +
                ", action=" + action +
                ", actionDate=" + actionDate +
                ", topic=" + topic +
                ", message=" + message +
                '}';
    }


    public static enum Action {
        ACTION_MEMBER_LEFT(0),
        ACTION_MEMBER_JOINED(1),
        ACTION_MEMBER_APPLIED(2),
        ACTION_MEMBER_INVITED(3),
        ACTION_MEMBER_DECLINED_BY_ADMIN(4),
        ACTION_MEMBER_REFUSED_TO_JOIN(5),
        ACTION_MEMBER_REMOVED(6),
        ACTION_NEW_ADMIN(7),
        ACTION_TOPIC_CREATED(8),
        ACTION_TOPIC_DELETED(9),
        ACTION_NEW_MESSAGE(10);

        private int id;

        private Action(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

    public static void log(User user, TeamMemberActivity.Action action, Command team) {
        log(user, action, team, null, null);
    }

    public static void log(User user, TeamMemberActivity.Action action, Command team, Topic topic) {
        log(user, action, team, topic, null);
    }

    public static void log(User user, TeamMemberActivity.Action action, Command team, Topic topic, TopicMessage message) {
        if(user == null || team == null) return;

        Logger.debug("[TEAM_LOG] %s: user[%d], team[%d]", action.toString(), user.id, team.id);

        TeamMemberActivity activity;
        switch (action) {
            case ACTION_MEMBER_LEFT:
            case ACTION_MEMBER_JOINED:
            case ACTION_MEMBER_APPLIED:
            case ACTION_MEMBER_INVITED:
            case ACTION_MEMBER_DECLINED_BY_ADMIN:
            case ACTION_MEMBER_REFUSED_TO_JOIN:
            case ACTION_MEMBER_REMOVED:
            case ACTION_NEW_ADMIN:
                activity = new TeamMemberActivity(team, user, action.getId());
                activity.create();
                break;

            case ACTION_TOPIC_CREATED:
                activity = new TeamMemberActivity(team, user, action.getId(), topic);
                activity.create();
                break;

            case ACTION_TOPIC_DELETED:
                activity = new TeamMemberActivity(team, user, action.getId(), topic.name);
                activity.create();
                break;

            case ACTION_NEW_MESSAGE:
                activity = new TeamMemberActivity(team, user, action.getId(), topic, message);
                activity.create();
                break;
        }
    }

}
