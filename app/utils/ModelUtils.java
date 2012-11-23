package utils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.Answer;
import models.BSphere;
import models.BType;
import models.ExpFinance;
import models.ExpIT;
import models.ExpLegal;
import models.ExpManagement;
import models.ExpMarketing;
import models.ExpOther;
import models.ExpSale;
import models.Question;
import models.Test;
import models.User;
import models.UserLevel;
import models.predicate.FindAnswerByIdPredicate;
import models.predicate.FindAnswerByQuestionIdPredicate;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Years;

import play.data.validation.Validation;
import play.i18n.Lang;

public class ModelUtils {
	
	private ModelUtils(){
	}
	
	public static void calculateUsersAge(User user){
		DateTime now = new DateTime();
		user.age = Years.yearsBetween(new DateMidnight(user.birthday), now).getYears();
	}
	
	public static String replaceSpacesForI18n(String word){
		if(word != null){
			if(Lang.get().equals("ru")){
				return word.replaceAll(ApplicationConstants.BLANKSPACE, ApplicationConstants.UNDERLINING);
			}
		}
		return word;
	}
	
	public static User fillUser(User to, User from){
		to.passwordHash = from.passwordHash;
		to.name = from.name;
		to.lastName = from.lastName;
		to.city = from.city;
		if(from.businessType != null && from.businessType.id != null)
			to.businessType = BType.findById(from.businessType.id);
		if(from.businessSphere != null && from.businessSphere.id != null)
			to.businessSphere = BSphere.findById(from.businessSphere.id);
		to.expMarketing = from.expMarketing;
		to.expFinance = from.expFinance;
		to.expIT = from.expIT;
		to.expLegal = from.expLegal;
		to.expManagement = from.expManagement;
		to.expSale = from.expSale;
		to.expOther = from.expOther;
		UserLevel none = UserLevel.findById(1l);
		to.expMarketing = new ExpMarketing();
		to.expMarketing.level = none;
		to.expFinance = new ExpFinance();
		to.expFinance.level = none;
		to.expIT = new ExpIT();
		to.expIT.level = none;
		to.expLegal = new ExpLegal();
		to.expLegal.level = none;
		to.expManagement = new ExpManagement();
		to.expManagement.level = none;
		to.expSale = new ExpSale();
		to.expSale.level = none;
		to.expOther = new ExpOther();
		to.expOther.level = none;
		//------
//		if(from.expMarketing.level.id != null) {
//			to.expMarketing.level = UserLevel.findById(from.expMarketing.level.id);
//		} else {
//			to.expMarketing.level = null;
//		}
//		if(from.expFinance.level.id != null) {
//			to.expFinance.level = UserLevel.findById(from.expFinance.level.id);
//		} else {
//			to.expFinance.level = null;
//		}
//		if(from.expIT.level.id != null) {
//			to.expIT.level = UserLevel.findById(from.expIT.level.id);;
//		} else {
//			to.expIT.level = null;
//		}
//		if(from.expLegal.level.id != null) {
//			to.expLegal.level = UserLevel.findById(from.expLegal.level.id);
//		} else {
//			to.expLegal.level = null;
//		}
//		if(from.expManagement.level.id != null) {
//			to.expManagement.level = UserLevel.findById(from.expManagement.level.id);
//		} else {
//			to.expManagement.level = null;
//		}
//		if(from.expSale.level.id != null){
//			to.expSale.level = UserLevel.findById(from.expSale.level.id);
//		} else {
//			to.expSale.level = null;
//		}
//		if(from.expOther.level.id != null) {
//			to.expOther.level = UserLevel.findById(from.expOther.level.id);
//		} else {
//			to.expOther.level = null;
//		}
		return to;
	}
}
