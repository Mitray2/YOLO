package utils;

import org.apache.commons.lang.StringUtils;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {

	public static final int[] MONTH_DAYS = new int[] { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
    public static final int MILLIS_PER_HOUR = 60 * 60 * 1000;
    public static final String USER_TIMEZONE_KEY = "tz";

	public static final String[] MONTH_NAMES = new String[] { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November",
			"December" };

    public static final SimpleDateFormat defDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat defTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH':'mm':'ss");

	public static boolean isLeapYear(Integer year) {
		return year != null ? year % 4 == 0 : false;
	}

	public static String getFormatedStringDate(Date date, boolean withTime) {
		DateFormatSymbols symbols = new DateFormatSymbols();
		symbols.setShortMonths(MONTH_NAMES);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd" + (withTime ? " HH':'mm" : StringUtils.EMPTY), new Locale("ru"));
		return date == null ? "" : formatter.format(date);
	}

	public static String getFormatedStringDate(Long time, boolean withTime) {
		DateFormatSymbols symbols = new DateFormatSymbols();
		symbols.setShortMonths(MONTH_NAMES);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd" + (withTime ? " HH':'mm':'ss" : StringUtils.EMPTY), new Locale("ru"));
		return time == null ? "" : formatter.format(new Date(time));
	}

	public static String format(Long time, boolean withTime, TimeZone timeZone) {
        if(time == null) return "";

		SimpleDateFormat formatter = (withTime ? defTimeFormat : defDateFormat);
        formatter.setTimeZone(timeZone);
		return formatter.format(new Date(time));
	}

	public static String getFormatedStringDateRu(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd MMM.", new Locale("ru"));
		return date == null ? "" : formatter.format(date);
	}

}
