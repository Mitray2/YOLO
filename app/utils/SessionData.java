package utils;

import models.User;

public class SessionData {

    public User user;

    public SessionUserMessage userMessage;

    public static class SessionUserMessage {
        public SessionUserMessage(String value) {
            this.value = value;
        }

        public String value;
        public String title;
    }
}
