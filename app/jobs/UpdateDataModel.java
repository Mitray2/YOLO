package jobs;

import java.util.List;

import models.Command;
import models.Post;
import models.Topic;
import models.User;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import utils.ModelUtils;

@OnApplicationStart
public class UpdateDataModel extends Job {

	public void doJob() {
		
		List<User> users = User.findAll();
		for (User user : users) {
			if (user.english == null) {
				user.english = false;
				user.save();
			}
		}
		
		List<Command> commandsWithEmptyGlobal = Command.find("global is NULL").fetch();
		for (Command c : commandsWithEmptyGlobal) {
			c.global = false;
			c.save();
		}
		
		List<Post> postsWithEmptyLang = Post.find("lang is NULL").fetch();
		for (Post post : postsWithEmptyLang) {
			post.lang = 0;
			post.save();
		}
		
		List<Command> commands = Command.findAll();
		for (Command command : commands) {
			for (Topic topic : command.topics) {
				if (topic.mainTopic) {
					topic.publicTopic = false;
					topic.groupId = command.id;
					topic.save();
				}
			}
			// creating public main topic
			Topic mainPublicTopic = new Topic();
			mainPublicTopic.mainTopic = true;
			mainPublicTopic.publicTopic = true;
			mainPublicTopic.groupId = command.id;
			mainPublicTopic.save();
			
			command.topics.add(mainPublicTopic);
			command.save();
		}
		
	}

}
