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
	public static void checkSecutiry() {
        Logger.info("SEC CHECK");
		User currentUser = SessionHelper.getCurrentUser(session);
		if (currentUser == null)
			CommonController.error(CommonController.ERROR_SECURITY);
		if (currentUser != null) {
			if (currentUser.email.equals(ApplicationConstants.ADMIN_EMAIL) && !request.path.startsWith(ApplicationConstants.ADMIN_PATH_STARTS_WITH))
				redirect(ApplicationConstants.ADMIN_PATH);
			User user = User.findById(currentUser.id);
			user.lastSeen = new Date();
			user.save();
		}
		if (currentUser.role == User.ROLE_INPERFECT_USER) {
			redirect(request.getBase() + ApplicationConstants.BLANK_FORM_PATH);
		}
		if (currentUser.role.equals(User.ROLE_WITHOUT_BLANK)) {
			redirect(request.getBase() + ApplicationConstants.BLANK_FORM_PATH);
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



    /*public static void editProfile(User user) {
		User currentUser = User.findById(SessionHelper.getCurrentUser(session).getId());
		// validation.required("user.city",
		// user.city).message(ApplicationConstants.VALIDATION_MODEL_USER_CITY_REQUIRED);
		// if (!validation.hasError("user.city"))
		// validation.maxSize("user.city", user.city,
		// 30).message(ApplicationConstants.VALIDATION_MODEL_USER_CITY_MAX_LENGTH);
		// if (!validation.hasError("user.city"))
		// validation.match("user.city", user.city,
		// ApplicationConstants.CITY_PATTERN).message(ApplicationConstants.VALIDATION_MODEL_USER_CITY_INVALID);
		//
		// validation.maxSize("user.expMarketing",
		// user.expMarketing.description,
		// 300).message(ApplicationConstants.VALIDATION_MODEL_USER_EXP_MARKETING_MAX_LENGTH);
		// validation.maxSize("user.expSale", user.expSale.description,
		// 300).message(ApplicationConstants.VALIDATION_MODEL_USER_EXP_SALE_MAX_LENGTH);
		// validation.maxSize("user.expManagement",
		// user.expManagement.description,
		// 300).message(ApplicationConstants.VALIDATION_MODEL_USER_EXP_MANAGEMENT_MAX_LENGTH);
		// validation.maxSize("user.expFinance", user.expFinance.description,
		// 300).message(ApplicationConstants.VALIDATION_MODEL_USER_EXP_FINANCE_MAX_LENGTH);
		// validation.maxSize("user.expLegal", user.expLegal.description,
		// 300).message(ApplicationConstants.VALIDATION_MODEL_USER_EXP_LEGAL_MAX_LENGTH);
		// validation.maxSize("user.expIT", user.expIT.description,
		// 300).message(ApplicationConstants.VALIDATION_MODEL_USER_EXP_IT_MAX_LENGTH);
		// validation.maxSize("user.expOther", user.expOther.description,
		// 300).message(ApplicationConstants.VALIDATION_MODEL_USER_EXP_OTHER_MAX_LENGTH);
		// validation.maxSize("user.personalCV", user.personalCV,
		// 300).message(ApplicationConstants.VALIDATION_MODEL_USER_PERSONAL_CV_MAX_LENGTH);

		if (validation.hasErrors()) {
			user.name = currentUser.name;
			user.lastName = currentUser.lastName;
			user.email = currentUser.email;
			user.id = currentUser.id;
			user.sex = currentUser.sex;
			user.birthday = currentUser.birthday;
			user.regDate = currentUser.regDate;
			user.pragmatist = currentUser.pragmatist;
			user.idealist = currentUser.idealist;
			user.communicant = currentUser.communicant;
			render("UserController/index.html", validation.errors(), user);
		}

		currentUser.expMarketing.level = user.expMarketing.level;
		currentUser.expMarketing.description = user.expMarketing.description;

		currentUser.expSale.level = user.expSale.level;
		currentUser.expSale.description = user.expSale.description;

		currentUser.expFinance.level = user.expFinance.level;
		currentUser.expFinance.description = user.expFinance.description;

		currentUser.expLegal.level = user.expLegal.level;
		currentUser.expLegal.description = user.expLegal.description;

		currentUser.expManagement.level = user.expManagement.level;
		currentUser.expManagement.description = user.expManagement.description;

		currentUser.expIT.level = user.expIT.level;
		currentUser.expIT.description = user.expIT.description;

		currentUser.expOther.level = user.expOther.level;
		currentUser.expOther.description = user.expOther.description;
		currentUser.expOther.expName = user.expOther.expName;

		currentUser.country = user.country;
		currentUser.businessType = user.businessType;
		currentUser.businessSphere = user.businessSphere;
		currentUser.city = user.city;
		currentUser.personalCV = user.personalCV;
		currentUser.showEmailForOthers = user.showEmailForOthers;
		// updating a user
		currentUser.save();

		SessionHelper.setCurrentUser(session, currentUser);
		SettingsController.settings();
	}*/


	/*public static void joinGroup(Long groupId, String text) {
		User user = SessionHelper.getCurrentUser(session);
		if (user != null) {
			Command group = Command.findById(groupId);
			if (group.getUsersForAprove() == null) {
				group.usersForAprove = new ArrayList<User>();
			}
			User userCurrent = User.findById(user.id);
			group.usersForAprove.add(userCurrent);
			userCurrent.commandToInvite = group;
			userCurrent.command = null;
			userCurrent.save();
			group.save();
			SessionHelper.setCurrentUser(session, userCurrent);
			User userAdmin = (User) User.find("role=? and command.id=?", 3, group.id).fetch().get(0);
			Mails.memberRequest(userAdmin, request.getBase(), userCurrent, text);
			SessionHelper.setUserMessage(session, new SessionUserMessage(MESSAGE_USER_CONTROLLER_REQUEST_SENT));
		}

		index(user.id);
	}

	public static void cancelJoinGroup(Long groupId) {
		User user = SessionHelper.getCurrentUser(session);
		if (user != null) {
			Command group = Command.findById(groupId);

			User userCurrent = User.findById(user.id);
			userCurrent.commandToInvite = null;
			userCurrent.save();
			group.save();
			SessionHelper.setCurrentUser(session, userCurrent);
		}
		index(user.id);
	}

	public static void approveInviteGroup(Long groupId) {
		Command group = Command.findById(groupId);
		User user = User.findById(SessionHelper.getCurrentUser(session).id);
    if (user.role == User.ROLE_GROUP_ADMIN) {
      SessionHelper.setCurrentUser(session, user);
      index(user.id);
    }
		user.command = group;
		user.commandToInvite = null;
		int lengthCommands = user.commandsForAprove.size();
		for (int i = 0; i < lengthCommands; i++) {
			if (user.commandsForAprove.get(i).id.equals(groupId)) {
				user.commandsForAprove.remove(i);
				user.save();
				break;
			}
		}
		SessionHelper.setCurrentUser(session, user);
		index(user.id);
	}

	public static void declineInviteGroup(Long groupId) {
		User user = User.findById(SessionHelper.getCurrentUser(session).id);
		int lengthCommands = user.commandsForAprove.size();
		for (int i = 0; i < lengthCommands; i++) {
			if (user.commandsForAprove.get(i).id.equals(groupId)) {
				user.commandsForAprove.remove(i);
				user.save();
				break;
			}
		}
		SessionHelper.setCurrentUser(session, user);
		index(user.id);
	}

	public static void exitGroup() {
		User user = User.findById(SessionHelper.getCurrentUser(session).id);
    if (user.command == null) {
      index(user.id);
    }
    if (User.ROLE_GROUP_ADMIN == user.role) {
      GroupController.index(user.command.id);
    }
		user.command = null;
		user.save();
		SessionHelper.setCurrentUser(session, user);
		index(user.id);
	}*/

}