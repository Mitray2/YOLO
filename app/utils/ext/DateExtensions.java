package utils.ext;

import org.apache.commons.lang.StringUtils;
import play.mvc.Http;
import play.templates.JavaExtensions;
import utils.DateUtils;

import java.util.TimeZone;

/** Helper methods to deal with dates in play templates */
public class DateExtensions extends JavaExtensions {

    public static String format(Long time, Http.Request req) {
        if(time == null) return "";

        return DateUtils.format(time, true, getUserTimeZone(req));
    }

    public static String formatDate(Long time, Http.Request req) {
        if(time == null) return "";

        return DateUtils.format(time, false, getUserTimeZone(req));
    }


    /* private stuff */

    private static TimeZone getUserTimeZone(Http.Request req){
        TimeZone userTimeZone = TimeZone.getTimeZone("GMT");
        if(req.cookies.containsKey(DateUtils.USER_TIMEZONE_KEY)){
            String tz = req.cookies.get(DateUtils.USER_TIMEZONE_KEY).value;
            if(!StringUtils.isEmpty(tz) && StringUtils.isNumeric(tz)) {
                userTimeZone.setRawOffset(Integer.valueOf(tz) * DateUtils.MILLIS_PER_HOUR);
            }
        }
        return userTimeZone;
    }
}


