package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import play.db.jpa.Model;

@Entity
public class Question extends Model {

	@ManyToOne
	public Test test;

	@Lob
	public String value;
	
	@OneToMany(mappedBy="question", cascade=CascadeType.ALL)
	public List<Answer> answers;

	@Transient
	public Answer answer;

	@OneToMany(mappedBy="question", cascade=CascadeType.ALL)
	public List<Question> questions;
	
	@ManyToOne
	public Question question;
	
}
