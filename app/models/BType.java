package models;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class BType extends Model {

	public String name;

	@Override
	public String toString() {
		return name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
