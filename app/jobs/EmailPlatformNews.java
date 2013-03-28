package jobs;

import models.NotificationType;
import models.Post;
import models.User;
import notifiers.Mails;
import play.Logger;
import play.db.jpa.JPA;
import play.jobs.Every;
import play.jobs.Job;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;

/** Sends latest platform new to user emails **/
public class EmailPlatformNews {

	public static void doJob(Post post) {
        Logger.debug("------------ JOB: platform news ------------");

        List<User> users = User.find("select u from User u join u.notifications n where n.id = ?", NotificationType.PLATFORM_NEWS).fetch();
        if(users.size() > 0){
            List<Long> userIds = extract(users, on(User.class).id);
            Logger.debug("JOB: found %d users to notify with platform news: %s", users.size(), Arrays.toString(userIds.toArray()));

            for (User user : users) {
                Mails.platformNews(user, post);
            }
        } else {
            Logger.debug("JOB: no users to notify of platform news :(");
        }

        Logger.debug("---------------------------------------------");
	}

}
