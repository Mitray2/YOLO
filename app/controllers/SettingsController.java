package controllers;

import modelDTO.UserSkillDTO;
import models.*;
import notifiers.Mails;
import play.Logger;
import play.i18n.Messages;
import play.mvc.Before;
import utils.ApplicationConstants;
import utils.ModelUtils;
import utils.SecurityHelper;
import utils.SessionData.SessionUserMessage;
import utils.SessionHelper;

import java.util.*;

import static utils.ApplicationConstants.*;

public class SettingsController extends BasicController {

	@Before
	public static void checkSecurity() {
		User currentUser = SessionHelper.getCurrentUser(session);
		if (currentUser == null){
			CommonController.error(CommonController.ERROR_SECURITY);
        } else {
			if (currentUser.email.equals(ApplicationConstants.ADMIN_EMAIL) &&
                    !request.path.startsWith(ApplicationConstants.ADMIN_PATH_STARTS_WITH)){

				redirect(ApplicationConstants.ADMIN_PATH);
            }
            // TODO refactor to single update query and move to UserDAO
			User user = User.findById(currentUser.id);
			user.lastSeen = new Date();
			user.save();

            if (currentUser.role == User.ROLE_INPERFECT_USER) {
                redirect(request.getBase() + ApplicationConstants.BLANK_FORM_PATH);
            }
            //TODO remove, role ROLE_WITHOUT_BLANK is not used in project any more
            if (currentUser.role.equals(User.ROLE_WITHOUT_BLANK)) {
                redirect(request.getBase() + ApplicationConstants.BLANK_FORM_PATH);
            }
		}
	}

	public static void settings() {
		User user = SessionHelper.getCurrentUser(session);
		if (user != null) {
		    render(user);
        } else {
            Logger.info("NO SESSION USER!!! redirect to main page");
            ApplicationController.index();
        }
	}


	public static void editContactData(String name, String lastName, boolean sex, Long countryId, String city,
                                       boolean showEmailForOthers, int birthYear, int birthMonth, int birthDay,
                                       Set<Long> notifications) {
		User currentUser = User.findById(SessionHelper.getCurrentUser(session).getId());
		validation.required("city", city).message(VALIDATION_MODEL_USER_CITY_REQUIRED);
		if (!validation.hasError("city"))
			validation.maxSize("city", city, 30).message(VALIDATION_MODEL_USER_CITY_MAX_LENGTH);
//		if (!validation.hasError("city"))
//			validation.match("city", city, ApplicationConstants.CITY_PATTERN).message(ApplicationConstants.VALIDATION_MODEL_USER_CITY_INVALID);

        Calendar birthday = Calendar.getInstance();
        //birthday.set(Integer.parseInt(birthYear), Integer.parseInt(birthMonth), Integer.parseInt(birthDay));
        birthday.set(birthYear, birthMonth, birthDay);
        currentUser.birthday = birthday.getTime();
        ModelUtils.calculateUsersAge(currentUser);

		Country country = Country.findById(countryId);
		currentUser.name = name;
		currentUser.lastName = lastName;
		currentUser.sex = sex;
		currentUser.country = country;
		currentUser.city = city;
		currentUser.showEmailForOthers = showEmailForOthers;
		currentUser.save();

		SessionHelper.setCurrentUser(session, currentUser);
		SettingsController.settings();
	}


	public static void setNotifications(Set<Long> nTypes){
        if(nTypes != null){
            Logger.info("nTypes: " + Arrays.toString(nTypes.toArray()));

            User user = User.findById(SessionHelper.getCurrentUser(session).id);

            List<NotificationType> newNotifications = new ArrayList<NotificationType>();
            for (Long nTypeId : nTypes) {
                newNotifications.add((NotificationType) NotificationType.findById(nTypeId));
            }
            user.notifications = newNotifications;
            user.save();

            SessionHelper.setCurrentUser(session, user);

        }

        SettingsController.settings();
    }

	public static void doChangePassword(String oldPassword, String newPassword,
			String newPasswordRepeat) {
		User currentUser = null;
		validation.required(oldPassword).message(Messages.get("page.profile.change.password.mesage1"));
		validation.required(newPassword).message(Messages.get("page.profile.change.password.mesage2"));
		if (!validation.hasError("newPassword"))
			validation.required(newPasswordRepeat).message(Messages.get("page.profile.change.password.mesage3"));
		if (!validation.hasError("oldPassword")) {
			currentUser = SessionHelper.getCurrentUser(session);
			validation.equals(SecurityHelper.createPasswordHash(oldPassword), currentUser.passwordHash).message(Messages.get("page.profile.change.password.mesage4"));
		}
		if (!validation.hasError("newPassword")) {
			validation.minSize(newPassword, 6).message(Messages.get("page.profile.change.password.mesage5"));
			validation.maxSize(newPassword, 12).message(Messages.get("page.profile.change.password.mesage6"));
		}
		if (!validation.hasError("newPassword")) {
			validation.match(newPassword, "[A-Za-z0-9\\.\\-\\_]+").message(Messages.get("page.profile.change.password.mesage7"));
		}
		if (!validation.hasError("newPassword") && !validation.hasError("newPasswordRepeat"))
			validation.equals(newPassword, newPasswordRepeat).message(Messages.get("page.profile.change.password.mesage8"));
		if (!validation.hasErrors()) {
			User user = User.findById(currentUser.id);
			user.passwordHash = SecurityHelper.createPasswordHash(newPassword);
			currentUser.passwordHash = user.passwordHash;
			user.save();
			//index(SessionHelper.getCurrentUser(session).id);
            SettingsController.settings();
		} else {
			User user = User.findById(SessionHelper.getCurrentUser(session).id);
			render("UserController/index.html", validation.errors(), user);
		}
	}

}