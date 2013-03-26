package models;

import play.db.jpa.Model;

import javax.persistence.Entity;

@Entity
public class NotificationType extends Model {

	public String name;

	@Override
	public String toString() {
		return name;
	}

}
