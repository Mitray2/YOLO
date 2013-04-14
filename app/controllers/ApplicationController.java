package controllers;

import models.Command;
import models.User;
import play.cache.Cache;
import utils.RssHelper;
import utils.SessionHelper;

import static utils.ApplicationConstants.CACHE_COMMANDS_COUNT;
import static utils.ApplicationConstants.CACHE_USERS_COUNT;

public class ApplicationController extends BasicController {

    /*@Catch(value = Throwable.class, priority = 1)
    public static void onError(Throwable e) {
        Logger.error(e, "[AppCTR] %s", e.getMessage());

        User user = SessionHelper.getCurrentUser(session);
        if (user != null) {
            UserController.index(user.id);
        } else {
            error();
        }
    }*/

    public static void index() {
     // boolean validBeta = false;
     //   if ("sntbeta".equals(request.params.get("betaCode"))) {
     //     response.setCookie("validBeta", "true", "100d");
     //     validBeta = true;
     //   }
        User user = SessionHelper.getCurrentUser(session);
        if (user != null) {
            UserController.index(user.id);
        } else {
            user = new User();
            Long usersCount = (Long) Cache.get(CACHE_USERS_COUNT);
            if (usersCount == null) {
                usersCount = User.count();
                Cache.add(CACHE_USERS_COUNT, usersCount, "1h");
            }
            Long commandsCount = (Long) Cache.get(CACHE_COMMANDS_COUNT);
            if (commandsCount == null) {
                commandsCount = Command.count();
                Cache.add(CACHE_COMMANDS_COUNT, commandsCount, "1h");
            }
            render(user, usersCount, commandsCount /*, validBeta*/);
        }
        render(/*validBeta*/);
    }

    public static void rss() {
      renderText(RssHelper.getInstance(request.getBase()).getRss());
    }

}