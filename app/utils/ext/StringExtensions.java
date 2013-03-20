package utils.ext;


import org.apache.commons.lang.StringUtils;
import play.templates.JavaExtensions;

/** Helper methods to deal with strings in play templates */
public class StringExtensions extends JavaExtensions {

    public static String limitTo(String text, int maxLength) {
        if(StringUtils.isEmpty(text)) return StringUtils.EMPTY;

        if (text.length() > maxLength){
            text = text.substring(0,maxLength).concat("...");
            int lastSpaceInd = text.lastIndexOf(" ");
            if(lastSpaceInd < text.length() - 1) {
                text = text.substring(0,lastSpaceInd).replaceAll("[.,;]$","");
            }
            text = text.concat("...");
        }
        return text;
    }
}


