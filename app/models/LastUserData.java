package models;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class LastUserData extends Model {

	public Long userId;
	public String name;
	public String lastName;

	public Long commandId;
	public Date lastSeen;
}
