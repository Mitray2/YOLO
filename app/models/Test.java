package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

@Entity
public class Test extends Model {

	public Integer number;

	@OneToMany(mappedBy = "test", cascade = CascadeType.ALL)
	public List<Question> questions;

	@Lob
	public String description;

}
