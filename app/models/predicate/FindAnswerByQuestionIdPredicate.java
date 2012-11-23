package models.predicate;

import models.Answer;

import org.apache.commons.collections.Predicate;

public class FindAnswerByQuestionIdPredicate implements Predicate{
	
	Long questionId;
	
	public void setQuestionId(Long questionId) {
		this.questionId = questionId;
	}
	
	@Override
	public boolean evaluate(Object answer) {
		return ((Answer)answer).question.id.equals(questionId);
	}

}
