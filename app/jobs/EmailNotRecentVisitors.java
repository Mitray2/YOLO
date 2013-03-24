package jobs;

import models.User;
import play.jobs.Job;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/** Change the age for users at 24pm every day **/
//@Every("12h")
public class EmailNotRecentVisitors extends Job {

	public void doJob() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -7);
        Date weekAgo = calendar.getTime();
		List<User> users = User.find("lastSeen < ?", weekAgo).fetch();
		for (User user : users) {
            //Mails.newUserMessages(user, userMessages);
		}
	}

}
