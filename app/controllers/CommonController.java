package controllers;

import play.mvc.Controller;
import utils.SessionHelper;

public class CommonController extends Controller {

	public static final String ERROR_UNDEFINED = "error.undefined";
	public static final String ERROR_SECURITY = "error.security";
	public static final String ERROR_UNAUTHORIZED_USER = "error.unauthorized_user";

	public static void error(String error) {
		String message = "Произошла неизвестная ошибка.";
		if (ERROR_SECURITY.equals(error)) {
			message = "У вас недостаточно прав для данного действия.";
		}
		if (ERROR_UNAUTHORIZED_USER.equals(error)) {
			message = "Сначала вы должны войти в систему.";
		}
//		SessionHelper.setUserMessage(session, message);
		ApplicationController.index();
	}

}
