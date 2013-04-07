package controllers;

import modelDTO.UserSkillDTO;
import models.*;
import notifiers.Mails;
import play.Logger;
import play.db.jpa.JPA;
import play.i18n.Messages;
import play.mvc.Before;
import utils.ApplicationConstants;
import utils.SecurityHelper;
import utils.SessionData.SessionUserMessage;
import utils.SessionHelper;

import javax.persistence.Query;
import java.util.*;

public class UserController extends BasicController  implements ApplicationConstants {

	@Before
	public static void checkSecurity() {
		// TODO warnings on page
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

	public static void index(Long userId) {
		User user = null;
		if (userId != null) {
			user = User.findById(userId);
			if (SessionHelper.getCurrentUser(session).id.equals(userId)) {
				SessionHelper.setCurrentUser(session, user);
			}
		}
		if (user == null) {
			user = User.findById(SessionHelper.getCurrentUser(session).id);
			SessionHelper.setCurrentUser(session, user);
		}
//		LastUserData lUserData = null;
//		String statement = "select l from LastUserData as l where l.userId=?";
//		List<LastUserData> lastSeenUserDatas = LastUserData.find(statement, user.id).fetch();
//		if (!lastSeenUserDatas.isEmpty()) {
//			lUserData = LastUserData.findById(lastSeenUserDatas.get(0).id);
//		}
		render(user);
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
		user.english= currentUser.english;
		user.save();

		SessionHelper.setCurrentUser(session, user);
		index(user.id);
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
			User userAdmin = (User) User.find("role=? and command.id=?", User.ROLE_GROUP_ADMIN, group.id).first();
			Mails.memberRequest(userAdmin, request.getBase(), userCurrent, text);
			SessionHelper.setUserMessage(session, new SessionUserMessage(MESSAGE_USER_CONTROLLER_REQUEST_SENT));
            index(user.id);
		}

        GroupController.index(groupId);

	}

	public static void cancelJoinGroup(Long groupId) {
		User user = SessionHelper.getCurrentUser(session);
		if (user != null) {
			Command group = Command.findById(groupId);
            if(group != null){
                User userCurrent = User.findById(user.id);
                userCurrent.commandToInvite = null;
                userCurrent.save();
                group.save();

                Mails.teamInvitationDeclined(group.founderUser, user);

                SessionHelper.setCurrentUser(session, userCurrent);
            }
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

        if(group != null){
            Mails.teamInvitationAccepted(group.founderUser, user);
        }

		SessionHelper.setCurrentUser(session, user);
		index(user.id);
	}

	public static void declineInviteGroup(Long groupId) {
        Command group = Command.findById(groupId);
		User user = User.findById(SessionHelper.getCurrentUser(session).id);
		int lengthCommands = user.commandsForAprove.size();
		for (int i = 0; i < lengthCommands; i++) {
			if (user.commandsForAprove.get(i).id.equals(groupId)) {
				user.commandsForAprove.remove(i);
				user.save();
				break;
			}
		}

        if(group != null){
            Mails.teamInvitationDeclined(group.founderUser, user);
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

        logGroupMemberActivity(user, user.command, TeamMemberActivity.ACTION_MEMBER_LEFT);

		user.command = null;
		user.save();
		SessionHelper.setCurrentUser(session, user);
		index(user.id);
	}

    private static void logGroupMemberActivity(User user, Command team, int action) {
        TeamMemberActivity activity = new TeamMemberActivity(team, user, action, new Date());
        activity.create();
    }

}