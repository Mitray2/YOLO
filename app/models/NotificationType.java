package models;

import play.db.jpa.Model;

import javax.persistence.Entity;

@Entity
public class NotificationType extends Model {

    public static final Long UNREAD_MESSAGES = 1L;
    public static final Long PLATFORM_NEWS = 2L;
    public static final Long GROUP_ACTIVITY = 3L;
    public static final Long REMINDERS = 4L;

	public String name;

	@Override
	public String toString() {
		return name;
	}

}
