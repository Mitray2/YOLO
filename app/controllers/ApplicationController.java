package controllers;

import models.Command;
import models.User;
import play.cache.EhCacheImpl;
import utils.RssHelper;
import utils.SessionHelper;

import static utils.ApplicationConstants.CACHE_COMMANDS_COUNT;
import static utils.ApplicationConstants.CACHE_USERS_COUNT;

public class ApplicationController extends BasicController {

    public static void index() {
      boolean validBeta = false;
        if ("12345678".equals(request.params.get("betaCode"))) {
          response.setCookie("validBeta", "true", "100d");
          validBeta = true;
        }
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
            render(user, usersCount, commandsCount, validBeta);
        }
        render(validBeta);
    }

    public static void rss() {
      renderText(RssHelper.getInstance(request.getBase()).getRss());
    }

}