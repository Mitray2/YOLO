package models;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class BSphere extends Model {

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String name;

	@Override
	public String toString() {
		return name;
	}

}
