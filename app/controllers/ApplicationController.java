package controllers;

import models.Command;
import models.User;
import static utils.ApplicationConstants.*;
import play.Logger;
import play.cache.EhCacheImpl;
import play.mvc.Controller;
import utils.RssHelper;
import utils.SessionHelper;

public class ApplicationController extends Controller {

	public static void index() {
		
		User user = SessionHelper.getCurrentUser(session);
		if (user != null) {
			UserController.index(user.id);
		} else {
			user = new User();
			Long usersCount = (Long) EhCacheImpl.getInstance().get(CACHE_USERS_COUNT);
			if (usersCount == null) {
				usersCount = User.count();
				EhCacheImpl.getInstance().add(CACHE_USERS_COUNT, usersCount, 60 * 60);
			}
			Long commandsCount = (Long) EhCacheImpl.getInstance().get(CACHE_COMMANDS_COUNT);
			if (commandsCount == null) {
				commandsCount = Command.count();
				EhCacheImpl.getInstance().add(CACHE_COMMANDS_COUNT, commandsCount, 60 * 60);
			}
			render(user, usersCount, commandsCount);
		}
		render();
	}

    public static void rss(){
        renderText(RssHelper.getInstance().getRss());
    }

}