package jobs;

import java.util.List;

import models.User;
import play.jobs.Job;
import play.jobs.On;
import utils.ModelUtils;

/** Change the age for users at 24pm every day **/
@On("0 0 0 * * ?")
public class Bootstrap extends Job {

	public void doJob() {
		List<User> users = User.findAll();
		for (User user : users) {
			ModelUtils.calculateUsersAge(user);
			user.save();
		}
	}

}
