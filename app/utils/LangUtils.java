package utils;

import org.apache.commons.lang.StringUtils;

import play.i18n.Lang;
import play.mvc.Http.Cookie;
import play.mvc.Http.Request;
import play.mvc.Http.Response;

public class LangUtils {
	public static String getNewsLang(String lang, Request request, Response response, String cookieName) {
		//by default news lang the same as user locale
		String newsLang = Lang.get();

		//check if another news lang saved in cookies
		Cookie newsLangCookie = request.cookies.get(cookieName);
		if (newsLangCookie != null && StringUtils.isNotEmpty(newsLangCookie.value)) {
			newsLang = newsLangCookie.value;
		}
		
		if (StringUtils.isNotEmpty(lang)) {
			//save selected news lang to cookies
			response.setCookie(cookieName, lang);
			newsLang = lang;
		}
		
		return newsLang;
	}
	
	public static Integer langToId(String lang) {
		return lang.equals("ru") ? 0 : 1;
	}
}