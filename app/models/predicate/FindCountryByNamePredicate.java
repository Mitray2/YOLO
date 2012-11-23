package models.predicate;

import models.Country;

import org.apache.commons.collections.Predicate;

public class FindCountryByNamePredicate implements Predicate{

	private String countryName;
	
	public FindCountryByNamePredicate(String countryName){
		this.countryName = countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}
	
	@Override
	public boolean evaluate(Object country) {
		return ((Country)country).name.equals(countryName);
	}
	
}
