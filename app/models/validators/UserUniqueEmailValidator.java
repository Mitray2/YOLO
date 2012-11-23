package models.validators;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;

import models.User;

import play.data.validation.Check;

public class UserUniqueEmailValidator extends Check{

	@Override
	public boolean isSatisfied(Object validatedObject, Object value) {
		List<User> users = User.findAll();
		users.remove((User)validatedObject);
		final String stringValue = (String) value; 
		return !CollectionUtils.exists(users, new Predicate() {
			
			@Override
			public boolean evaluate(Object obj) {
				User user = (User) obj;
				return  user.email != null && user.email.equals(stringValue);
			}
		});
	}
}
