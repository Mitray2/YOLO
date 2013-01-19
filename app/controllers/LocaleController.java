package controllers;

import org.apache.commons.lang.StringUtils;

import play.Play;
import play.i18n.Lang;
import play.mvc.Before;
import play.mvc.Controller;
import utils.SessionHelper;

public class LocaleController extends BasicController {
	
	public static void change(String locale) {
//		SessionHelper.setCurrentLocale(session, locale);
//		System.out.println("locale change to: " + locale);
		Lang.change(locale);
		System.out.println("locale change to: " + locale + " langs=" + Play.langs);
		renderText("ok");
	}
	
}
