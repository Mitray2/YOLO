package models;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

@Entity
public class Answer extends Model {

	public static final int USER_PARAMETER_PRAGMATIST = 0;
	public static final int USER_PARAMETER_COMMUNICANT = 1;
	public static final int USER_PARAMETER_IDEALIST = 2;
	
	@ManyToOne
	public Question question;

	public String value;

	public Float score;
	
	public Integer userParameter;
}
