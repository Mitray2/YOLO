package utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import models.User;

import org.jboss.netty.util.internal.ConcurrentHashMap;

import play.mvc.Scope.Session;
import utils.SessionData.SessionUserMessage;

public class SessionHelper {

	public static final String SESSION_ID = "psessionid";

	private static final Random rand = new Random();

	private static Map<String, SessionData> sessionStore = new ConcurrentHashMap<String, SessionData>();

	private static String generateSessionId() {
		return new Long(System.currentTimeMillis()).toString();
	}

	public static void setUserMessage(Session session, SessionUserMessage message) {
		checkInitialised(session);
		sessionStore.get(session.get(SESSION_ID)).userMessage = message;
	}

	public static SessionUserMessage getUserMessage(Session session) {
		checkInitialised(session);
		SessionUserMessage result = sessionStore.get(session.get(SESSION_ID)).userMessage;
		sessionStore.get(session.get(SESSION_ID)).userMessage = null;
		return result;
	}

	public static User getCurrentUser(Session session) {
		checkInitialised(session);
		return sessionStore.get(session.get(SESSION_ID)).user;
	}

	public static void setCurrentUser(Session session, User user) {
		checkInitialised(session);
		sessionStore.get(session.get(SESSION_ID)).user = user;
	}

  public static void updateUser(User user) {
    for (Map.Entry<String, SessionData> entry : sessionStore.entrySet()) {
      if (entry.getValue().user != null && user.id.equals(entry.getValue().user.id)) {
        entry.getValue().user = user;
      }
    }
  }

	private static void checkInitialised(Session session) {
		if (session.get(SESSION_ID) == null) {
			String sessionId = SessionHelper.generateSessionId();
			session.put(SessionHelper.SESSION_ID, sessionId);
		}
		if (sessionStore.get(session.get(SESSION_ID)) == null) {
			sessionStore.put(session.get(SESSION_ID), new SessionData());
		}
	}

	public static void logout(Session session) {
		checkInitialised(session);
		User currentUser = getCurrentUser(session);
		if(currentUser != null) {
//			currentUser.refresh();
//			currentUser.lastSeen = new Date();
//			currentUser.save();
		}
		sessionStore.remove(session.get(SESSION_ID));
	}

}
