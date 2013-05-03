package utils;

import junit.framework.Assert;
import org.junit.Test;

/**
 * TODO class javadoc
 */
public class URLUtilsTest {
    @Test
    public void testLinkify() throws Exception {
        String text = "Visit twitter@gmail.com! or your blog [ex. http://RegExr.com?2rjl6!] or www.whatever.com!";
        String textWithLinks = URLUtils.linkify(text);
        Assert.assertEquals(textWithLinks, "Visit <a href='mailto:twitter@gmail.com'>twitter@gmail.com</a>! or your " +
                "blog [ex. <a href='http://RegExr.com?2rjl6' target='_blank'>http://RegExr.com?2rjl6</a>!] " +
                "or <a href='http://www.whatever.com' target='_blank'>www.whatever.com</a>!");
    }
}
