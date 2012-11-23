package models.validators;

import java.text.ParseException;

import models.User;

import org.apache.commons.lang.time.DateUtils;

import play.data.validation.Check;

public class UserDateValidator extends Check{

	public static final String[] patterns = new String[]{"dd.MM.yyyy"};
	
	@Override
	public boolean isSatisfied(Object validatedObject, Object value) {
		try {
			String date = (String) value;
			User user = (User) validatedObject;
			user.birthday = DateUtils.parseDate(date, patterns);
			if(!date.split("\\.")[2].startsWith("19"))
				throw new ParseException(date, 2);
		} catch (ParseException e) {
			return false;
		}
		return true;
	}

}
