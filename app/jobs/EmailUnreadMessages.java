package jobs;

import models.Message;
import models.NotificationType;
import notifiers.Mails;
import play.Logger;
import play.db.jpa.JPA;
import play.jobs.Every;
import play.jobs.Job;

import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;


/** Sends to user an email with unread messages **/
@Every("1mn")
public class EmailUnreadMessages extends Job {

	public void doJob() {
        Logger.debug("------------ JOB:MSG------------");

		List<Message> unreadMessages = Message.find(
                "select m from Message m join m.to.notifications as n " +
                "where n.id = ? and m.isRead = 0 and m.isSent = 0 and TIMEDIFF(NOW(),m.time) > '00:05:00' and m.to.id <> deletedBy",
                NotificationType.UNREAD_MESSAGES).fetch();

        Logger.debug("[JOB:MSG]: found %d unread messages", unreadMessages.size());

        if(unreadMessages.size() > 0){
            for (Message unreadMessage : unreadMessages) {
                Logger.debug("[JOB:MSG]: user [%d] got new unread messages", unreadMessage.to.id);
                Mails.unreadMessage(unreadMessage);
            }

            final List<Long> unreadMessagesIds = extract(unreadMessages, on(Message.class).id);
            JPA.em().createQuery("update Message set isSent = 1 where id IN (:ids)")
                    .setParameter("ids", unreadMessagesIds)
                    .executeUpdate();
        }

        Logger.debug("----------------------------------------------");
    }
}
