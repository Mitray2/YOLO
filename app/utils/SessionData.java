package utils;

import java.util.List;
import java.util.Map;

import models.User;

public class SessionData {

	public User user;

	public SessionUserMessage userMessage;

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
