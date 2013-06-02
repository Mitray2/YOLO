package utils;

import org.apache.commons.lang.StringUtils;

/**
 * Contains helper funcs to deal with URLs
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

    public static String linkify(String inputText) {
        String replacedText, replacePattern1, replacePattern2, replacePattern3;
        //URLs starting with http://, https://, or ftp://
        replacePattern1 = "(?uim)(\\b(https?|ftp):\\/\\/[-A-Z0-9+&@#\\/%?=~_|!:,.;]*[-A-Z0-9+&@#\\/%=~_|])";
        replacedText = inputText.replaceAll(replacePattern1, "<a href='$1' target='_blank'>$1</a>");

        //URLs starting with "www." (without // before it, or it'd re-link the ones done above).
        replacePattern2 = "(?uim)(^|[^\\/])(www\\.[-A-Z0-9+&@#\\/%?=~_|!:,.;]*[-A-Z0-9+&@#\\/%=~_|]+(\\b|$))";
        replacedText = replacedText.replaceAll(replacePattern2, "$1<a href='http://$2' target='_blank'>$2</a>");

        //Change email addresses to mailto:: links.
        replacePattern3 = "(?uim)(\\w+@[a-zA-Z_]+?\\.[a-zA-Z]{2,6})";
        replacedText = replacedText.replaceAll(replacePattern3, "<a href='mailto:$1'>$1</a>");

        return replacedText; //.replaceAll("\n", "<br/>");
    }
}
