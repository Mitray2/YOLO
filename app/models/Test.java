package models;

import play.db.jpa.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class Test extends Model {

	public Integer number;

	@OneToMany(mappedBy = "test", cascade = CascadeType.ALL)
	public List<Question> questions;

	public String description;

}
