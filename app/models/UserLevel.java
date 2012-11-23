package models;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class UserLevel extends Model {

	public String userLevel;

	@Override
	public String toString() {
		return userLevel;
	}
	
}
