package controllers;

import modelDTO.SimpleMessage;
import models.Message;
import models.User;
import play.Logger;
import play.db.jpa.JPA;
import utils.SessionHelper;

import javax.persistence.Query;
import java.util.*;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

/** Controller logic to handle user dialogs management */
public class MessageController extends BasicController {

    public static final int TALKS_TO_PAGE_LIMIT = 10;
    private static final Comparator<Message> messageComparator = new Comparator<Message>() {
        @Override
        public int compare(Message msg1, Message msg2) {
            return msg2.time.getTime() < msg1.time.getTime() ? 1 :
                   msg2.time.getTime() > msg1.time.getTime() ? -1 : 0;
        }
    };


    /** Lists user conversations */
	public static void index(Long userId){
        if (userId != null) {
            User user = SessionHelper.getCurrentUser(session);
            if (user != null && userId.equals(user.id)) {
                String querySQL = String.format("select id, isRead, text, time, from_id, to_id, deletedBy from ( select * from (" +
                        "(select *, to_id as other from (select * from Message where from_id = %d and deletedBy <> %d order by time desc) as ms group by from_id, to_id)" +
                        " union " +
                        "(select *, from_id as other from (select * from Message where to_id = %d and deletedBy <> %d order by time desc) as ms group by from_id, to_id)" +
                        ") as res order by time DESC) as m group by other  order by time desc", userId, userId, userId, userId);
                List<Message> conversations = JPA.em().createNativeQuery(querySQL, Message.class).getResultList();

                render(user, conversations);
            }
        }

        UserController.index(userId);
	}


    /** Retrieves single conversation with other user */
    public static void talk(Long userToTalkId, int page, int limit){
        boolean returnJson = request.headers.get("accept").value().contains("application/json");
        if(returnJson) Logger.debug("return talks as JSON");

        if (userToTalkId != null) {
            User user = SessionHelper.getCurrentUser(session);
            User userToTalk = User.findById(userToTalkId);
            if (user != null && userToTalk != null) {
                if (userToTalkId.equals(user.id)) {
                    if(returnJson) renderJSON("[]");
                    else MessageController.index(user.id);
                } else {
                    if (limit <= 0) limit = TALKS_TO_PAGE_LIMIT;
                    String queryGetLast = "select m from Message m where ((m.from.id = ? and m.to.id = ?) or (m.from.id = ? and m.to.id = ?)) and m.deletedBy != ? order by time DESC";
                    List<Message> conversations = Message.find(queryGetLast, user.id, userToTalkId, userToTalkId, user.id, user.id).fetch(page, limit);
                    Collections.sort(conversations, messageComparator);

                    markAllAsRead(conversations, user.id);

                    if(returnJson) {
                        List<SimpleMessage> messages = new ArrayList<SimpleMessage>();
                        for(Message msg : conversations) {
                            messages.add(SimpleMessage.fromFullMessage(msg, false));
                        }
                        renderJSON(messages);
                    } else {
                        render(user, userToTalk, conversations);
                    }
                }
            }
        }

        if(returnJson) renderJSON("[]");
        else UserController.index(userToTalkId);
    }


    /** Lists the newest messages in conversation with other user */
    public static void getNewMessages(Long userId, Long userToTalkId, long time){
        renderJSON(getUserNewMessages(userId,userToTalkId,time));
    }


    /** Lists the newest messages recently sent to current user */
    public static List<SimpleMessage> getUserNewMessages(Long userId, Long userToTalkId, long time){
        //Logger.debug("userID: %s, userToTalkId: %s, time: %s", userId, userToTalkId, time);

        List<SimpleMessage> conversations = new ArrayList<SimpleMessage>();
        User user = SessionHelper.getCurrentUser(session);
        if(user != null && user.id.equals(userId)) {
            if(!userId.equals(userToTalkId)){
                boolean loadAll = userToTalkId == null;
                Date lastUpdated = new Date(time);

                StringBuilder query = new StringBuilder(
                        "select m from Message m where m.to.id = ? and m.isRead = 0 and m.deletedBy <> ? ")
                        .append( loadAll ? "" : " and m.from.id = ? ")
                        .append( time > 0 ? " and m.time > ? " : "")
                        .append(" order by m.time ASC");

                //Logger.debug("q(newMessages) = %s", query);

                List<Message> messages;
                if(time > 0){
                    if(loadAll){
                        messages = Message.find(query.toString(), user.id, user.id, lastUpdated).fetch();
                    } else {
                        messages = Message.find(query.toString(), user.id, user.id, userToTalkId, lastUpdated).fetch();
                    }
                } else {
                    if(loadAll){
                        messages = Message.find(query.toString(), user.id, user.id).fetch();
                    } else {
                        messages = Message.find(query.toString(), user.id, user.id, userToTalkId).fetch();
                    }
                }

                if(!loadAll) {
                    markAllAsRead(messages, user.id);
                }

                //if(messages.size() > 0) Logger.debug("%d new messages found for user [%d]", messages.size(), userId);

                for(Message msg : messages) {
                    conversations.add(SimpleMessage.fromFullMessage(msg, loadAll));
                }
            }
        }

        return conversations;
    }


    /** Creates new user message on the database */
    public static void sendMessage(Long fromId, Long toId, String msg, long time) {
        User user = SessionHelper.getCurrentUser(session);
        if(user != null && user.id.equals(fromId)) {
            Message message = new models.Message();
            message.text = msg;                     // TODO validate on not required and max size !!!
            message.from = new User(fromId);
            message.to = new User(toId);
            message.time = new Date(); //time > 0 ? new Date(time) :
            message.isRead = false;
            message.create();

            renderJSON("{\"status\": \"ok\", \"id\": " + message.id + "}");
        } else {
            renderJSON("{\"status\": \"not ok\"}");
        }
    }


    /** Deletes whole user conversation with the other user */
    public static void deleteConversation(Long userId, Long otherId) {
        User user = SessionHelper.getCurrentUser(session);
        if(user != null && otherId != null && !user.id.equals(otherId)) {
            //int result = Message.delete("delete from Message where (from_id = ? and to_id = ?) or (from_id = ? and to_id = ?)", userId, otherId, otherId, userId);
            Query updQuery = JPA.em().createQuery("update Message set deletedBy = ? where ((from_id = ? and to_id = ?) or (from_id = ? and to_id = ?)) and deletedBy = 0");
            updQuery.setParameter(1, userId);
            updQuery.setParameter(2, userId);
            updQuery.setParameter(3, otherId);
            updQuery.setParameter(4, otherId);
            updQuery.setParameter(5, userId);
            int result2 = updQuery.executeUpdate();

            int result1 = Message.delete("delete from Message where ((from_id = ? and to_id = ?) or (from_id = ? and to_id = ?)) and deletedBy = ?", userId, otherId, otherId, userId, otherId);
            Logger.debug("conversation between %d and %d erased: %d messages", userId, otherId, result1);                                   //todo |^ check if that null clause needed
            Logger.debug("conversation between %d and %d deleted: %d messages", userId, otherId, result2);

            renderJSON("{\"status\": \"ok\"}");
        } else {
            renderJSON("{\"status\": \"not ok\"}");
        }
    }


    /** Deletes single user message */
    public static void deleteMessage(Long id) {
        User user = SessionHelper.getCurrentUser(session);
        if(user != null && id != null) {
            Message msg = Message.findById(id);
            if(msg.deletedBy != user.id){
                if(msg.deletedBy == 0){
                    msg.deletedBy = user.id;
                    msg.save();
                }else{
                    msg.delete();
                }
            }

            renderJSON("{\"status\": \"ok\"}");
        } else {
            renderJSON("{\"status\": \"not ok\"}");
        }
    }


    /** Returns other user from given conversation */
    public static User getOtherCommunicant(User user, models.Message message) {
        return user.id.equals(message.from.id) ? message.to : message.from;
    }


    private static void markAllAsRead(List<Message> messages, Long currentUserId){
        List<Message> unreadMessages = filter(allOf(having(on(Message.class).isRead, is(false)), having(on(Message.class).to.id, is(currentUserId))), messages);
        List<Long> unreadMessagesIds = extract(unreadMessages, on(Message.class).id);

        if(unreadMessagesIds.size() > 0){
            Query queryMarkAllRead = JPA.em().createQuery("update Message set isRead = 1 where id IN (:ids)");
            queryMarkAllRead.setParameter("ids", unreadMessagesIds);
            int result = queryMarkAllRead.executeUpdate();
            Logger.debug("%d updated: %s", result, Arrays.toString(unreadMessagesIds.toArray()));
        }
    }


}
