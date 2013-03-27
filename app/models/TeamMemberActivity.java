package models;

import play.db.jpa.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
public class TeamMemberActivity extends Model {

    public static final int ACTION_MEMBER_LEFT = 0;
    public static final int ACTION_MEMBER_JOINED = 1;

    @ManyToOne
	public Command team;

    @ManyToOne
	public User user;
	public int action;
	public Date actionDate;

    public TeamMemberActivity() {
    }

    public TeamMemberActivity(Command team, User user, int action, Date actionDate) {
        this.team = team;
        this.user = user;
        this.action = action;
        this.actionDate = actionDate;
    }

    @Override
    public String toString() {
        return "TeamMemberActivity{" +
                "team=" + team +
                ", user=" + user +
                ", action=" + action +
                ", actionDate=" + actionDate +
                '}';
    }
}
