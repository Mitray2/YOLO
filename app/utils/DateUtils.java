package utils;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

public class DateUtils {

	public static final int[] MONTH_DAYS = new int[] { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

	public static final String[] MONTH_NAMES = new String[] { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November",
			"December" };

	public static boolean isLeapYear(Integer year) {
		return year != null ? year % 4 == 0 : false;
	}

	public static String getFormatedStringDate(Date date, boolean withTime) {
		DateFormatSymbols symbols = new DateFormatSymbols();
		symbols.setShortMonths(MONTH_NAMES);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd" + (withTime ? " HH':'mm" : StringUtils.EMPTY), new Locale("ru"));
		return date == null ? "" : formatter.format(date);
	}

	public static String getFormatedStringDateRu(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd MMM.", new Locale("ru"));
		return date == null ? "" : formatter.format(date);
	}

}
