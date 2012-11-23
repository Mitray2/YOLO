package utils;

import java.util.List;

import models.User;

public class SessionData {

	public User user;

	public SessionUserMessage userMessage;

	public List<Long> testsNotPassed = null;

	public int indexCurrentTest;

	public int cap1;

	public int cap2;

	public String capMsg;
	
	public static class SessionUserMessage {
//		int type;
		public SessionUserMessage(String value){
//			this(value, null);
			this.value = value;
//			this.title = title;
		}
		
//		public SessionUserMessage(String value, String title){
//			this.value = value;
//			this.title = title;
//		}
		
		public String value;
		public String title;
	}
}
