package utils;

import models.*;
import models.comparators.CountryComparator;
import models.predicate.FindCountryByNamePredicate;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Years;
import play.i18n.Lang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
			if(Lang.get().equals("en")){
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
		to.expMarketing = new ExpMarketing();
        UserLevel none = UserLevel.findById(1l);
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
        to.english = from.english;
		return to;
	}

    /**
     * Used to render countries in right order on view
     * @return
     */
    public static List<Country> getSortedCountries(){
        List<Country> countries = Country.findAll();
        List<Country> result = new ArrayList<Country>(countries);

        Collections.sort(result, new CountryComparator());

        FindCountryByNamePredicate findCountryPredicate = new FindCountryByNamePredicate("Ukraine");
        result.add(0, result.remove(result.indexOf(CollectionUtils.find(result, findCountryPredicate))));
        findCountryPredicate.setCountryName("Kazakhstan");
        result.add(0, result.remove(result.indexOf(CollectionUtils.find(result, findCountryPredicate))));
        findCountryPredicate.setCountryName("Belarus");
        result.add(0, result.remove(result.indexOf(CollectionUtils.find(result, findCountryPredicate))));
        findCountryPredicate.setCountryName("Russia");
        result.add(0, result.remove(result.indexOf(CollectionUtils.find(result, findCountryPredicate))));

        return result;
    }

}
