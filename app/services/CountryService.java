package services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import models.Country;
import models.comparators.CountryComparator;
import models.predicate.FindCountryByNamePredicate;

import org.apache.commons.collections.CollectionUtils;

import play.i18n.Messages;
import utils.ApplicationConstants;

public class CountryService {
	
	private CountryService(){}
	
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
