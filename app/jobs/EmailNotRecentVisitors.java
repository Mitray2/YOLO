package jobs;

import models.User;
import notifiers.Mails;
import play.Logger;
import play.jobs.Every;
import play.jobs.Job;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/** Sends email notification to users who hasn't been to site for more than a week **/
@Every("10mn")
public class EmailNotRecentVisitors extends Job {

	public void doJob() {
        Logger.debug("------------ JOB: not seen users ------------");

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -7);
        Date weekAgo = calendar.getTime();

		List<User> users = User.find("lastSeen < ?", weekAgo).fetch();
        Logger.debug("JOB: found %d not recently seen users", users.size());
        Mails.notSeenForAWeek(users.get(0));
		for (User user : users) {
            //Mails.notSeenForAWeek(user);
		}
        Logger.debug("---------------------------------------------");
	}

}
