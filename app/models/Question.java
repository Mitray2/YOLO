package models;

import play.db.jpa.Model;

import javax.persistence.*;
import java.util.List;

@Entity
public class Question extends Model {

	@ManyToOne
	public Test test;

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
