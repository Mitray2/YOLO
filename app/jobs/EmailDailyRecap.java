package jobs;

import modelDTO.TeamActivity;
import modelDTO.UserActivity;
import models.Command;
import models.NotificationType;
import models.TeamMemberActivity;
import models.User;
import notifiers.Mails;
import play.Logger;
import play.db.jpa.JPA;
import play.jobs.Job;
import play.jobs.On;

import javax.persistence.Query;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/** Sends email notification for team related activity **/
@On("0 0 21 * * ?") // fires every day at 21:00 MSK
public class EmailDailyRecap extends Job {

	public void doJob() {
        Logger.debug("------------ JOB: user activity ------------");
        final NotificationType ntDailyRecap = NotificationType.findById(NotificationType.DAILY_RECAP);

        // 1. get teams updated in last 24 hours (also with new topics and union members tracking)
        Query q = JPA.em().createNativeQuery("select DISTINCT c.id from Command c " +
                "join Topic t on t.team_id = c.id " +
                "join TopicMessage m on m.topic_id = t.id " +
                "where m.createDate > DATE_ADD(NOW(),INTERVAL -1 DAY) " +
                "UNION " +
                "select DISTINCT c.id from Command c " +
                "join TeamMemberActivity tma " +
                "on c.id = tma.team_id " +
                "and tma.actionDate > DATE_ADD(NOW(),INTERVAL -1 DAY) " +
                "UNION " +
                "select DISTINCT c.id from Command c  " +
                "join Topic t on t.team_id = c.id " +
                "where t.createdDateTime > DATE_ADD(NOW(),INTERVAL -1 DAY)");

        List<Long> updatedTeamsIDs = new ArrayList<Long>();
        for(Object res: q.getResultList()){
            updatedTeamsIDs.add(((BigInteger) res).longValue());
        }
        Logger.debug("updated teams ids = " + Arrays.toString(updatedTeamsIDs.toArray()));

        ConcurrentMap<User, UserActivity> userActivity = new ConcurrentHashMap<User, UserActivity>();
        if (updatedTeamsIDs.size() > 0){
            List<Command> updatedTeams = Command.find("id IN (?1)", updatedTeamsIDs).fetch();

            for (Command team : updatedTeams) {
                // 2. for each team get team JOINED members
                Integer joinedMembers = ((Number) JPA.em().createNativeQuery(
                        String.format("select count(*) from TeamMemberActivity tma " +
                            "where tma.team_id = %d and tma.actionDate > DATE_ADD(NOW(),INTERVAL -1 DAY) and tma.action = %d",
                            team.id, TeamMemberActivity.Action.ACTION_MEMBER_JOINED.getId())).getSingleResult()).intValue();

                // 3. for each team get team LEFT members
                Integer leftMembers = ((Number) JPA.em().createNativeQuery(
                        String.format("select count(*) from TeamMemberActivity tma " +
                                "where tma.team_id = %d and tma.actionDate > DATE_ADD(NOW(),INTERVAL -1 DAY) and tma.action = %d",
                                team.id, TeamMemberActivity.Action.ACTION_MEMBER_LEFT.getId())).getSingleResult()).intValue();


                // 4. for each team get COUNT of recently added TOPICS
                Integer newTopics = ((Number) JPA.em().createNativeQuery(
                        String.format("select count(t.id) from Topic t " +
                                "where t.team_id = %d and t.createdDateTime > DATE_ADD(NOW(),INTERVAL -1 DAY)", team.id)
                        ).getSingleResult()).intValue();

                // 5. for each team get COUNT of recently added MESSAGES
                Integer newTeamMessages = ((Number) JPA.em().createNativeQuery(
                        String.format("select count(m.id) from TopicMessage m \n" +
                                "join Topic t on (t.team_id = %d and t.id = m.topic_id)\n" +
                                "where m.createDate > DATE_ADD(NOW(),INTERVAL -1 DAY)", team.id)
                        ).getSingleResult()).intValue();

                for(User user: team.users){
                    if(user.notifications.contains(ntDailyRecap)){
                        Logger.debug("user [%d] of team [%d] WANTS to receive group notifications", user.id, team.id);

                        UserActivity activity = new UserActivity();
                        activity.teamActivity = new TeamActivity(team, newTopics, newTeamMessages, joinedMembers, leftMembers);
                        userActivity.put(user, activity);
                    } else {
                        Logger.debug("user [%d] of team [%d] doesn't want to receive group notifications", user.id, team.id);
                    }
                }
            }
        }

        for (Map.Entry<User, UserActivity> activity : userActivity.entrySet()) {
            Logger.debug("JOB: notify user [%d] for %s", activity.getKey().id, activity.getValue().toString());
            Mails.recentUserActivity(activity.getKey(), activity.getValue());
        }

        Logger.debug("----------------------------------------------");
	}

}
