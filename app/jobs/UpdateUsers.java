package jobs;

import java.util.List;

import models.User;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import utils.ModelUtils;

@OnApplicationStart
public class UpdateUsers extends Job {

	public void doJob() {
		List<User> users = User.findAll();
		for (User user : users) {
			if (user.english == null) {
				user.english = false;
				user.save();
			}
		}
	}

}
