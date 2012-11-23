package utils;

import org.apache.commons.lang.StringUtils;

public class StringUtilsDisplay {
	public static String getSubString(String str, int start, int end) {
		return str == null ? "" : StringUtils.substring(str, start, end);
	}
}
