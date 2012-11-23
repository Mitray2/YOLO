package models;

import javax.persistence.Entity;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Country extends Model{
		
		@Required
		public String name;
		
		@Override
		public String toString() {
		return name;
		}
}
