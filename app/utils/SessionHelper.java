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

	public static String generateCaptcha(Session session) {
		checkInitialised(session);
		SessionData data = sessionStore.get(session.get(SESSION_ID));
		data.cap1 = rand.nextInt(10);
		data.cap2 = rand.nextInt(10);
		data.capMsg = data.cap1 + " + " + data.cap2;
		return data.capMsg;
	}

	public static boolean validateCaptcha(Session session, String cap) {
		checkInitialised(session);
		SessionData data = sessionStore.get(session.get(SESSION_ID));
		return cap != null
				&& cap.equals(new Integer(data.cap1 + data.cap2).toString());
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

	public static List<Long> getTestsNotPassed(Session session) {
		checkInitialised(session);
		List<Long> testsPassed = sessionStore.get(session.get(SESSION_ID)).testsNotPassed;
		return testsPassed;
	}

	public static void setTestsNotPassed(Session session, Long id) {
		checkInitialised(session);
		if (sessionStore.get(session.get(SESSION_ID)).testsNotPassed == null) {
			sessionStore.get(session.get(SESSION_ID)).testsNotPassed = new ArrayList<Long>();
		}
		sessionStore.get(session.get(SESSION_ID)).testsNotPassed.add(id);
	}

	public static void removeTestPassed(Session session, int id) {
		checkInitialised(session);
		sessionStore.get(session.get(SESSION_ID)).testsNotPassed.remove(id);
	}

	public static void setIndexCurrentTest(Session session, int index) {
		checkInitialised(session);
		sessionStore.get(session.get(SESSION_ID)).indexCurrentTest = index;
	}

	public static int getIndexCurrentTest(Session session) {
		checkInitialised(session);
		return sessionStore.get(session.get(SESSION_ID)).indexCurrentTest;
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
