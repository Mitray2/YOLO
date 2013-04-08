package jobs;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimaps;
import modelDTO.TeamActivity;
import modelDTO.UserActivity;
import models.*;
import notifiers.Mails;
import play.Logger;
import play.db.jpa.JPA;
import play.jobs.Job;
import play.jobs.On;

import javax.persistence.Query;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/** Sends email notification for user related activity **/
@On("0 0 21 * * ?") // fires every day at 21:00 MSK
public class EmailUserActivity extends Job {


    private static Function<Message, User> pickToUserFn = new Function<Message, User>() {
        @Override
        public User apply(Message message) {
            return message.to;
        }
    };

	public void doJob() {
        Logger.debug("------------ JOB: user activity ------------");
        final NotificationType ntUnreadMessages = NotificationType.findById(NotificationType.UNREAD_MESSAGES);
        final NotificationType ntGroupActivity = NotificationType.findById(NotificationType.GROUP_ACTIVITY);

        // 1. get teams updated in last 24 hours (also with new topics and union members tracking)
        Query q = JPA.em().createNativeQuery("select DISTINCT c.id from Command c " +
                "join Topic t on t.groupId = c.id " +
                "join TopicMessage m on m.topic_id = t.id " +
                "where m.createDate > DATE_ADD(NOW(),INTERVAL -1 DAY) " +
                "UNION " +
                "select DISTINCT c.id from Command c " +
                "join TeamMemberActivity tma " +
                "on c.id = tma.team_id " +
                "and tma.actionDate > DATE_ADD(NOW(),INTERVAL -1 DAY) " +
                "UNION " +
                "select DISTINCT c.id from Command c  " +
                "join Topic t on t.groupId = c.id " +
                "where t.createdDateTime > DATE_ADD(NOW(),INTERVAL -1 DAY)");

        List<Long> updatedTeamsIDs = new ArrayList<Long>();
        for(Object res: q.getResultList()){
            updatedTeamsIDs.add(((BigInteger) res).longValue());
        }
        Logger.debug("updated teams ids = " + Arrays.toString(updatedTeamsIDs.toArray()));


        ConcurrentMap<User, UserActivity> userActivity = new ConcurrentHashMap<User, UserActivity>();
        if (updatedTeamsIDs.size() > 0){
            //List<Command> updatedTeams = JPA.em().createQuery("from Command where id in (:ids)").setParameter("ids", updatedTeamsIDs).getResultList();
            List<Command> updatedTeams = Command.find("id IN (?1)", updatedTeamsIDs).fetch();

            for (Command team : updatedTeams) {
                // 2. for each team get team JOINED members
                Integer joinedMembers = ((Number) JPA.em().createNativeQuery(
                        String.format("select count(*) from TeamMemberActivity tma " +
                            "where tma.team_id = %d and tma.actionDate > DATE_ADD(NOW(),INTERVAL -1 DAY) and tma.action = %d",
                            team.id, TeamMemberActivity.ACTION_MEMBER_JOINED)).getSingleResult()).intValue();

                // 3. for each team get team LEFT members
                Integer leftMembers = ((Number) JPA.em().createNativeQuery(
                        String.format("select count(*) from TeamMemberActivity tma " +
                                "where tma.team_id = %d and tma.actionDate > DATE_ADD(NOW(),INTERVAL -1 DAY) and tma.action = %d",
                                team.id, TeamMemberActivity.ACTION_MEMBER_LEFT)).getSingleResult()).intValue();


                // 4. for each team get COUNT of recently added TOPICS
                Integer newTopics = ((Number) JPA.em().createNativeQuery(
                        String.format("select count(t.id) from Topic t " +
                                "where t.groupId = %d and t.createdDateTime > DATE_ADD(NOW(),INTERVAL -1 DAY)", team.id)
                        ).getSingleResult()).intValue();

                // 5. for each team get COUNT of recently added MESSAGES
                Integer newTeamMessages = ((Number) JPA.em().createNativeQuery(
                        String.format("select count(m.id) from TopicMessage m \n" +
                                "join Topic t on (t.groupId = %d and t.id = m.topic_id)\n" +
                                "where m.createDate > DATE_ADD(NOW(),INTERVAL -1 DAY)", team.id)
                        ).getSingleResult()).intValue();

                for(User user: team.users){
                    if(user.notifications.contains(ntGroupActivity)){
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

        // 6. get all unread messages at the moment
		List<Message> unreadMessages = Message.find("isRead = 0 and to.id <> deletedBy").fetch();
        Logger.debug("JOB: found %d unread messages", unreadMessages.size());

        ImmutableListMultimap<User, Message> userMessagesMap = Multimaps.index(unreadMessages, pickToUserFn);
        for (Map.Entry<User, Collection<Message>> userMessages : userMessagesMap.asMap().entrySet()) {
            User user = userMessages.getKey();
            if(user.notifications.contains(ntUnreadMessages)){
                Logger.debug("user [%d] WANTS to receive unread messages", user.id);

                List<Message> messages = new LinkedList<Message>(userMessages.getValue());
                if(messages.size() > 0) {

                    if(userActivity.containsKey(user)){
                        UserActivity activity  = userActivity.get(user);
                        activity.unreadMessages = messages;
                        userActivity.replace(user, activity);
                    }else{
                        userActivity.put(user, new UserActivity(messages));
                    }

                } else {
                    Logger.debug("JOB: no unread messages for user [%d]", user.id);
                }
            } else {
                Logger.debug("user [%d] doesn't want to receive unread messages", user.id);
            }
        }

        for (Map.Entry<User, UserActivity> activity : userActivity.entrySet()) {
            Logger.debug("JOB: notify user [%d] for %s", activity.getKey().id, activity.getValue().toString());
            Mails.recentUserActivity(activity.getKey(), activity.getValue());
        }

        Logger.debug("----------------------------------------------");
	}

}
