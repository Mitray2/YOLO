package controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import modelDTO.UserSkillDTO;
import models.BSphere;
import models.BType;
import models.Command;
import models.Country;
import models.LastUserData;
import models.User;
import models.UserLevel;
import notifiers.Mails;
import play.mvc.Before;
import play.mvc.Controller;
import utils.ApplicationConstants;
import utils.SecurityHelper;
import utils.SessionData.SessionUserMessage;
import utils.SessionHelper;

public class UserController extends Controller implements ApplicationConstants {

	@Before
	public static void checkSecutiry() {
		// TODO warnings on page
		User currentUser = SessionHelper.getCurrentUser(session);
		if (currentUser == null)
			CommonController.error(CommonController.ERROR_SECURITY);
		if (currentUser != null) {
			if (currentUser.email.equals(ApplicationConstants.ADMIN_EMAIL) && !request.path.startsWith(ApplicationConstants.ADMIN_PATH_STARTS_WITH))
				redirect(ApplicationConstants.ADMIN_PATH);
			LastUserData lastSeenUserData = null;
			String statement = "select l from LastUserData as l where l.userId=?";
			List<LastUserData> lastSeenUserDatas = LastUserData.find(statement, currentUser.id).fetch();
			if (lastSeenUserDatas.isEmpty()) {
				lastSeenUserData = new LastUserData();
				lastSeenUserData.userId = currentUser.id;
				lastSeenUserData.name = currentUser.name;
				lastSeenUserData.lastName = currentUser.lastName;
			} else {
				lastSeenUserData = LastUserData.findById(lastSeenUserDatas.get(0).id);
			}

			if (currentUser.command != null) {
				lastSeenUserData.commandId = currentUser.command.id;
			}
			lastSeenUserData.lastSeen = new Date();
			lastSeenUserData.save();
		}
		if (currentUser.role == User.ROLE_INPERFECT_USER) {
			redirect(request.getBase() + ApplicationConstants.SECOND_TEST_PATH);
		}
		if (currentUser.role.equals(User.ROLE_WITHOUT_BLANK)) {
			redirect(request.getBase() + ApplicationConstants.BLANK_FORM_PATH);
		}

	}

	public static final void index(Long userId) {
		User user = null;
		if (userId != null) {
			user = User.findById(userId);
			if (SessionHelper.getCurrentUser(session).id == userId) {
				SessionHelper.setCurrentUser(session, user);
			}
		}
		if (user == null) {
			user = User.findById(SessionHelper.getCurrentUser(session).id);
			SessionHelper.setCurrentUser(session, user);
		}
		LastUserData lUserData = null;
		String statement = "select l from LastUserData as l where l.userId=?";
		List<LastUserData> lastSeenUserDatas = LastUserData.find(statement, user.id).fetch();
		if (!lastSeenUserDatas.isEmpty()) {
			lUserData = LastUserData.findById(lastSeenUserDatas.get(0).id);
		}
		render(user, lUserData);
	}

	public static void editContactData(Long countryId, String city, boolean showEmailForOthers) {
		User currentUser = User.findById(SessionHelper.getCurrentUser(session).getId());
		validation.required("city", city).message(ApplicationConstants.VALIDATION_MODEL_USER_CITY_REQUIRED);
		if (!validation.hasError("city"))
			validation.maxSize("city", city, 30).message(ApplicationConstants.VALIDATION_MODEL_USER_CITY_MAX_LENGTH);
		if (!validation.hasError("city"))
			validation.match("city", city, ApplicationConstants.CITY_PATTERN).message(ApplicationConstants.VALIDATION_MODEL_USER_CITY_INVALID);
		Country country = Country.findById(countryId);
		currentUser.country = country;
		currentUser.city = city;
		currentUser.showEmailForOthers = showEmailForOthers;
		currentUser.save();

		SessionHelper.setCurrentUser(session, currentUser);
		index(currentUser.id);
	}

	public static void editProfile(User user) {
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
		index(currentUser.id);
	}

	public static void editSkill(UserSkillDTO currentUser) {
		User user = User.findById(SessionHelper.getCurrentUser(session).getId());

		// validation.maxSize("currentUser.expMarketing",
		// currentUser.expMarketing.description,
		// 300).message(ApplicationConstants.VALIDATION_MODEL_USER_EXP_MARKETING_MAX_LENGTH);
		// validation.maxSize("currentUser.expSale",
		// currentUser.expSale.description,
		// 300).message(ApplicationConstants.VALIDATION_MODEL_USER_EXP_SALE_MAX_LENGTH);
		// validation.maxSize("currentUser.expManagement",
		// currentUser.expManagement.description,
		// 300).message(ApplicationConstants.VALIDATION_MODEL_USER_EXP_MANAGEMENT_MAX_LENGTH);
		// validation.maxSize("currentUser.expFinance",
		// currentUser.expFinance.description,
		// 300).message(ApplicationConstants.VALIDATION_MODEL_USER_EXP_FINANCE_MAX_LENGTH);
		// validation.maxSize("currentUser.expLegal",
		// currentUser.expLegal.description,
		// 300).message(ApplicationConstants.VALIDATION_MODEL_USER_EXP_LEGAL_MAX_LENGTH);
		// validation.maxSize("currentUser.expIT",
		// currentUser.expIT.description,
		// 300).message(ApplicationConstants.VALIDATION_MODEL_USER_EXP_IT_MAX_LENGTH);
		// validation.maxSize("currentUser.expOther",
		// currentUser.expOther.description,
		// 300).message(ApplicationConstants.VALIDATION_MODEL_USER_EXP_OTHER_MAX_LENGTH);
		// validation.maxSize("currentUser.personalCV", currentUser.personalCV,
		// 300).message(ApplicationConstants.VALIDATION_MODEL_USER_PERSONAL_CV_MAX_LENGTH);

		if (validation.hasErrors()) {
			user.expMarketing.description = currentUser.expMarketing.description;
			user.expSale.description = currentUser.expSale.description;
			user.expManagement.description = currentUser.expManagement.description;
			user.expFinance.description = currentUser.expFinance.description;
			user.expLegal.description = currentUser.expLegal.description;
			user.expIT.description = currentUser.expIT.description;
			user.expOther.description = currentUser.expOther.description;
			user.personalCV = currentUser.personalCV;
			List<Country> countries = Country.findAll();
			List<BType> businessTypes = BType.findAll();
			List<BSphere> businessSpheres = BSphere.findAll();
			List<UserLevel> levels = UserLevel.findAll();
			render("UserController/index.html", validation.errors(), user, countries, businessTypes, businessSpheres, levels);
		}

		UserLevel marketingLevel = UserLevel.findById(currentUser.expMarketing.level.id);
		user.expMarketing.level = marketingLevel;
		user.expMarketing.description = currentUser.expMarketing.description;

		UserLevel saleLevel = UserLevel.findById(currentUser.expSale.level.id);
		user.expSale.level = saleLevel;
		user.expSale.description = currentUser.expSale.description;

		UserLevel financeLevel = UserLevel.findById(currentUser.expFinance.level.id);
		user.expFinance.level = financeLevel;
		user.expFinance.description = currentUser.expFinance.description;

		UserLevel legalLevel = UserLevel.findById(currentUser.expLegal.level.id);
		user.expLegal.level = legalLevel;
		user.expLegal.description = currentUser.expLegal.description;

		UserLevel managementLevel = UserLevel.findById(currentUser.expManagement.level.id);
		user.expManagement.level = managementLevel;
		user.expManagement.description = currentUser.expManagement.description;

		UserLevel ITLevel = UserLevel.findById(currentUser.expIT.level.id);
		user.expIT.level = ITLevel;
		user.expIT.description = currentUser.expIT.description;

		UserLevel otherLevel = UserLevel.findById(currentUser.expOther.level.id);
		user.expOther.level = otherLevel;
		user.expOther.description = currentUser.expOther.description;
		user.expOther.expName = currentUser.expOther.expName;

		BType bType = BType.findById(currentUser.businessType.id);
		BSphere bSphere = BSphere.findById(currentUser.businessSphere.id);

		// saving of a user
		user.businessType = bType;
		user.businessSphere = bSphere;
		user.personalCV = currentUser.personalCV;

		user.save();

		SessionHelper.setCurrentUser(session, user);
		index(user.id);
	}

	public static void doChangePassword(String oldPassword, String newPassword,
			String newPasswordRepeat) {
		User currentUser = null;
		validation.required(oldPassword).message("Вы не ввели старый пароль");
		validation.required(newPassword).message("Вы не ввели новый пароль");
		if (!validation.hasError("newPassword"))
			validation.required(newPasswordRepeat).message("Вы не повторили новый пароль");
		if (!validation.hasError("oldPassword")) {
			currentUser = SessionHelper.getCurrentUser(session);
			validation.equals(SecurityHelper.createPasswordHash(oldPassword), currentUser.passwordHash).message("Неверный старый пароль");
		}
		if (!validation.hasError("newPassword")) {
			validation.minSize(newPassword, 6).message("Минимальная длина пароля - 6 символов.");
			validation.maxSize(newPassword, 12).message("Максимальная длина пароля - 12 символов.");
		}
		if (!validation.hasError("newPassword")) {
			validation.match(newPassword, "[A-Za-z0-9\\.\\-\\_]+").message("Пароль может " + "содержать только цифры и символы латинского алфавита");
		}
		if (!validation.hasError("newPassword") && !validation.hasError("newPasswordRepeat"))
			validation.equals(newPassword, newPasswordRepeat).message("Пароли не совпадают");
		if (!validation.hasErrors()) {
			User user = User.findById(currentUser.id);
			user.passwordHash = SecurityHelper.createPasswordHash(newPassword);
			currentUser.passwordHash = user.passwordHash;
			user.save();
			index(SessionHelper.getCurrentUser(session).id);
		} else {
			User user = User.findById(SessionHelper.getCurrentUser(session).id);
			render("UserController/index.html", validation.errors(), user);
		}
	}

	public static void joinGroup(Long groupId, String text) {
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
			Mails.memberRequest(userAdmin, request.getBase(), userCurrent.name + " " + userCurrent.lastName, text);
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
		user.command = group;
		user.commandToInvite = null;
		int lengthCommands = user.commandsForAprove.size();
		for (int i = 0; i < lengthCommands; i++) {
			if (user.commandsForAprove.get(i).id == groupId) {
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
			if (user.commandsForAprove.get(i).id == groupId) {
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
		user.command = null;
		user.save();
		SessionHelper.setCurrentUser(session, user);
		index(user.id);
	}

}