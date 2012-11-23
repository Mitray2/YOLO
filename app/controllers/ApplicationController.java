package controllers;

import models.User;
import play.mvc.Controller;
import utils.SessionHelper;

public class ApplicationController extends Controller {

	public static void index() {
		
		User user = SessionHelper.getCurrentUser(session);
		if (user != null) {
			UserController.index(user.id);
		} else {
			user = new User();
			render(user);
		}
		render();
	}

}