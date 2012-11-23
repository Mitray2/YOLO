package models.predicate;

import models.Answer;

import org.apache.commons.collections.Predicate;

public class FindAnswerByIdPredicate implements Predicate{
	
	private long id;
	
	public void setId(long id) {
		this.id = id;
	}
	
	@Override
	public boolean evaluate(Object answer) {
		return ((Answer)answer).id.equals(id);
	}
	
}
