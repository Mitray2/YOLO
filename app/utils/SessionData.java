package utils;

import models.User;

import java.io.Serializable;

public class SessionData implements Serializable {

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
