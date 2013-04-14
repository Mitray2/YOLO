package utils;

import models.User;
import play.cache.Cache;
import play.mvc.Scope.Session;
import utils.SessionData.SessionUserMessage;

public class SessionHelper {

	public static final String SESSION_ID = "psessionid";

	//private static Map<String, SessionData> sessionStore = new ConcurrentHashMap<String, SessionData>();

    private static SessionData getSessionData(String key){
        return Cache.get(key, SessionData.class);
    }

    private static void removeSessionData(String key){
        Cache.delete(key);
    }

    private static void setSessionData(String key, SessionData data){
        Cache.set(key, data, "1d");
    }

	private static String generateSessionId() {
		return Long.toString(System.currentTimeMillis());
	}

	public static void setUserMessage(Session session, SessionUserMessage message) {
		checkInitialised(session);
		//sessionStore.get(session.get(SESSION_ID)).userMessage = message;

        SessionData data = getSessionData(session.get(SESSION_ID));
        data.userMessage = message;
        setSessionData(session.get(SESSION_ID), data);
	}

	public static SessionUserMessage getUserMessage(Session session) {
		checkInitialised(session);
		//SessionUserMessage result = sessionStore.get(session.get(SESSION_ID)).userMessage;
		//sessionStore.get(session.get(SESSION_ID)).userMessage = null;
		//return result;

        SessionData data = getSessionData(session.get(SESSION_ID));
        SessionUserMessage message = data.userMessage;
        data.userMessage = null;
        setSessionData(session.get(SESSION_ID), data);
        return message;

	}

	public static User getCurrentUser(Session session) {
		checkInitialised(session);
		//return sessionStore.get(session.get(SESSION_ID)).user;

		return getSessionData(session.get(SESSION_ID)).user;
	}

	public static void setCurrentUser(Session session, User user) {
		checkInitialised(session);
		//sessionStore.get(session.get(SESSION_ID)).user = user;

        SessionData data = getSessionData(session.get(SESSION_ID));
        data.user = user;
        final String userSessionId = session.get(SESSION_ID);
        setSessionData(userSessionId, data);
	}

  public static void updateUser(Session session, User user) {
    /*for (Map.Entry<String, SessionData> entry : sessionStore.entrySet()) {
      if (entry.getValue().user != null && user.id.equals(entry.getValue().user.id)) {
        entry.getValue().user = user;
      }
    }*/
      setCurrentUser(session, user);
  }

	private static void checkInitialised(Session session) {
        final String sessionId = session.get(SESSION_ID);
		if (sessionId == null) {
			String newSessionId = SessionHelper.generateSessionId();
			session.put(SessionHelper.SESSION_ID, newSessionId);
		}
		/*if (sessionStore.get(session.get(SESSION_ID)) == null) {
			sessionStore.put(session.get(SESSION_ID), new SessionData());
		}*/

        if (getSessionData(sessionId) == null) {
            setSessionData(sessionId, new SessionData());
        }
	}

	public static void logout(Session session) {
		checkInitialised(session);
		User currentUser = getCurrentUser(session);

		//sessionStore.remove(session.get(SESSION_ID));
        removeSessionData(session.get(SESSION_ID));
	}

}
