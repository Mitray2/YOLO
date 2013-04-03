package jobs;

import models.NotificationType;
import models.User;
import notifiers.Mails;
import play.Logger;
import play.db.jpa.JPA;
import play.jobs.Every;
import play.jobs.Job;
import utils.DateUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;

/** Sends email notification to users who hasn't been to site for more than a week **/
//@On("0 0 9 * * ?")
@Every("1h")
public class EmailNotRecentVisitors extends Job {

	public void doJob() {
        Logger.debug("------------ JOB: not seen users ------------");

        Date weekAgo = DateUtils.weekAgo();

		List<User> users = User.find("select u from User u join u.notifications n where u.lastSeen < ? and (u.lastNotified IS NULL or u.lastNotified < ?) and n.id = ?",
                weekAgo, weekAgo, NotificationType.REMINDERS).fetch();

        if(users.size() > 0){
            List<Long> userIds = extract(users, on(User.class).id);
            Logger.debug("JOB: found %d not recently seen users", users.size());

            for (User user : users) {
                Mails.notSeenForAWeek(user);
            }

            int result = JPA.em()
                    .createQuery("update User set lastNotified = NOW() where id IN (:ids)")
                    .setParameter("ids", userIds)
                    .executeUpdate();

            Logger.debug("%d updated: %s", result, Arrays.toString(userIds.toArray()));
        } else {
            Logger.debug("JOB: no users to notify, all are active :)");
        }

        Logger.debug("---------------------------------------------");
	}

}
