package jobs;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimaps;
import models.Message;
import models.User;
import notifiers.Mails;
import play.Logger;
import play.jobs.Every;
import play.jobs.Job;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.not;


/** Sends email notification for unread messages **/
@Every("5mn") //TODO for prod change it to: @On("0 0 8 * * ?")
public class EmailNewMessages extends Job {


    private static Function<Message, User> pickToUserFn = new Function<Message, User>() {
        @Override
        public User apply(Message message) {
            return message.to;
        }
    };

	public void doJob() {
        Logger.debug("------------ JOB: unread messages ------------");

		List<Message> unreadMessages = Message.find("isRead = 0").fetch();
        Logger.debug("JOD: found %d unread messages", unreadMessages.size());

        ImmutableListMultimap<User, Message> userMessagesMap = Multimaps.index(unreadMessages, pickToUserFn);
        for (Map.Entry<User, Collection<Message>> userMessages : userMessagesMap.asMap().entrySet()) {
            User user = userMessages.getKey();
            //List<Message> messages = new LinkedList<Message>(userMessages.getValue());
            List<Message> notDeletedMessages = filter(having(on(Message.class).deletedBy, not(user.id)), userMessages.getValue());
            if(notDeletedMessages.size() > 0) {
                Logger.debug("JOB: notify user [%d] for %d unread messages", user.id, notDeletedMessages.size());
                Mails.newUserMessages(user, notDeletedMessages);
            } else {
                Logger.debug("JOB: no unread messages for user [%d]", user.id);
            }
        }

        Logger.debug("----------------------------------------------");
	}

}
