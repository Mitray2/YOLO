package controllers.crud.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to indicate that data should be retrieved with defined parameters.
 * Example:
 * 	@ChoiceFilter(value="fieldName^fieldValueLike,value="fieldName^fieldValueLike")
 * 
 * @author zhenya-yadlovskij
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.PARAMETER})
public @interface ChoiceFilter {
	String value();
}
