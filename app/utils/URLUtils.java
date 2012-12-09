package utils;

import org.apache.commons.lang.StringUtils;

/**
 * Created with IntelliJ IDEA.
 * User: Evgeniy Yadlovskiy
 * Date: 09.12.12
 * Time: 20:36
 * To change this template use File | Settings | File Templates.
 */
public class URLUtils {

    public URLUtils() {
    }

    public static String getApplicationName(String baseURL){
        return getDomainName(baseURL).split("\\.")[0];
    }

    public static String getDomainName(String baseURL) {
        return baseURL.replaceFirst("http://", StringUtils.EMPTY);
    }
}
