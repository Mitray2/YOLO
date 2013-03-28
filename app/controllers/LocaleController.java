package controllers;

import play.Logger;
import play.Play;
import utils.LangUtils;

public class LocaleController extends BasicController {
	
	public static void change(String locale) {
        LangUtils.updateSystemLang(locale);
        Logger.debug("locale change to: " + locale + " langs=" + Play.langs);
		renderText("ok");
	}
	
}
