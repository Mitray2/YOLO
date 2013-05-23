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

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;

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
        to.english = from.english;

        to.businessType = BType.findById(from.businessType != null && from.businessType.id != null ? from.businessType.id : 1);
        to.businessSphere = BSphere.findById(from.businessSphere != null && from.businessSphere.id != null ? from.businessSphere.id : 1);

        UserLevel none = UserLevel.findById(1L);

        to.expFinance =     from.expFinance != null ?   from.expFinance     : new ExpFinance(none);
        to.expIT =          from.expIT != null ?        from.expIT          : new ExpIT(none);
        to.expLegal =       from.expLegal != null ?     from.expLegal       : new ExpLegal(none);
        to.expManagement =  from.expManagement != null ? from.expManagement : new ExpManagement(none);
        to.expMarketing =   from.expMarketing != null ? from.expMarketing   : new ExpMarketing(none);
        to.expOther =       from.expOther != null ?     from.expOther       : new ExpOther(none);
        to.expSale =        from.expSale != null ?      from.expSale        : new ExpSale(none);

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

    public static boolean isFavTeam(User user, Long teamId){
        final List<Long> teamIds = extract(user.favouriteTeams, on(Command.class).id);
        return teamIds.contains(teamId);
    }

    public static boolean isBlackTeam(User user, Long teamId){
        final List<Long> teamIds = extract(user.blacklistTeams, on(Command.class).id);
        return teamIds.contains(teamId);
    }

}
