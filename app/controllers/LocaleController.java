package controllers;

import org.apache.commons.lang.StringUtils;

import play.Play;
import play.i18n.Lang;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Http.Response;
import utils.SessionHelper;

public class LocaleController extends BasicController {
	
	public static void change(String locale) {
//		SessionHelper.setCurrentLocale(session, locale);
//		System.out.println("locale change to: " + locale);
		//Lang.change(locale);
		Lang.set(locale);
		Response.current().setCookie(Play.configuration.getProperty("application.lang.cookie", "PLAY_LANG"), locale, "365d");
		System.out.println("locale change to: " + locale + " langs=" + Play.langs);
		renderText("ok");
	}
	
}
