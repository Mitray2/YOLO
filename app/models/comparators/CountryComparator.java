package models.comparators;

import java.util.Comparator;

import play.i18n.Messages;
import utils.ApplicationConstants;
import utils.ModelUtils;

import models.Country;

public class CountryComparator implements Comparator<Country>{

	@Override
	public int compare(Country o1, Country o2) {
		return Messages.get(ApplicationConstants.MODELS_COUNTRY_NAME_PREFIX + ModelUtils.replaceSpacesForI18n(o1.name)).compareTo(Messages.get(ApplicationConstants.MODELS_COUNTRY_NAME_PREFIX + ModelUtils.replaceSpacesForI18n(o2.name)));
	}

}
