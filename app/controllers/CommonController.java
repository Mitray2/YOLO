package controllers;

import play.i18n.Messages;
import play.mvc.Controller;
import utils.SessionHelper;

public class CommonController extends BasicController {

	public static final String ERROR_UNDEFINED = "error.undefined";
	public static final String ERROR_SECURITY = "error.security";
	public static final String ERROR_UNAUTHORIZED_USER = "error.unauthorized_user";

	public static void error(String error) {
		String message = Messages.get("common.error.undefined");
		if (ERROR_SECURITY.equals(error)) {
			message = Messages.get("common.error.access");
		}
		if (ERROR_UNAUTHORIZED_USER.equals(error)) {
			message = Messages.get("common.error.need_login");
		}
//		SessionHelper.setUserMessage(session, message);
		ApplicationController.index();
	}

}
