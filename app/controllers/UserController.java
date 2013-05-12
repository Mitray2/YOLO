package controllers;

import modelDTO.UserSkillDTO;
import models.*;
import notifiers.Mails;
import play.db.jpa.JPA;
import play.mvc.Before;
import utils.ApplicationConstants;
import utils.SessionData.SessionUserMessage;
import utils.SessionHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserController extends BasicController  implements ApplicationConstants {

    public static final int TRACKED_TOPIC_CATEGORY_ALL = 0;
    public static final int TRACKED_TOPIC_CATEGORY_FAVOURITE = 1;
    public static final int TRACKED_TOPIC_CATEGORY_BLACKLIST = 2;

    public static final int TEAM_TOPICS_TO_PAGE_LIMIT = 10;

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

            if (currentUser.role == User.ROLE_INPERFECT_USER) {
                //redirect(request.getBase() + ApplicationConstants.BLANK_FORM_PATH);
                LoginController.notValidated();
            }
            if (currentUser.role.equals(User.ROLE_WITHOUT_BLANK)) {
                redirect(request.getBase() + ApplicationConstants.BLANK_FORM_PATH);
            }
		}
	}

	public static void index(Long userId) {
		User user = null;
		if (userId != null) {
			user = User.findById(userId);
			if (SessionHelper.getCurrentUser(session).id.equals(userId)) {
				SessionHelper.setCurrentUser(session, user);
                UserController.teamtrack(null, null);
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

    public static void profile() {
        User user = SessionHelper.getCurrentUser(session);
        render(user);
    }

    public static void teamtrack(Integer country, Integer category) {
        User user = User.findById(SessionHelper.getCurrentUser(session).id);
        List<Topic> topics = getTrackedTopics(user, country, category, null, null);

        render(user, topics, country, category);
    }

    public static void tracktopics(Integer country, Integer category, Integer offset, Integer limit) {
        User _user = User.findById(SessionHelper.getCurrentUser(session).id);
        List<Topic> _topics = getTrackedTopics(_user, country, category, offset, limit);

        render("tags/group/tracktopics.html", _user, _topics);
    }

    private static List<Topic> getTrackedTopics(User user, Integer country, Integer category, Integer offset, Integer limit){
        List<Topic> topics = new ArrayList<Topic>();

        if (limit == null || limit <= 0)    limit = TEAM_TOPICS_TO_PAGE_LIMIT;
        if (offset == null || offset <= 0)  offset =  0;

        String query;
        // 1. ALL: select all PUBLIC topics of ALL teams in ALL countries - EXCEPT BLACKLISTED
        if(country == null && (category == null || category.equals(TRACKED_TOPIC_CATEGORY_ALL))) {
            query = "SELECT t.* from Topic t " +
                    "where t.publicTopic = 1 and t.mainTopic = 0 and t.lastUpdateDate IS NOT NULL " +
                    "and (select count(*) from UserBlacklistTeam ubt where ubt.User_id = ? and ubt.Team_id = t.team_id) = 0 " +
                    "and (select count(*) from UserFavouriteTeam uft where uft.User_id = ? and uft.Team_id = t.team_id) = 0 " +
                    "ORDER BY t.lastUpdateDate DESC LIMIT ? OFFSET ?";

            topics = JPA.em().createNativeQuery(query, Topic.class)
                    .setParameter(1, user.id)
                    .setParameter(2, user.id)
                    .setParameter(3, limit)
                    .setParameter(4, offset)
                    .getResultList();
        } else {

            // 2. COUNTRY: select all PUBLIC topics of ALL teams in chosen COUNTRY - EXCEPT BLACKLISTED
            if(country != null && category == null || category.equals(TRACKED_TOPIC_CATEGORY_ALL)) {
                query = "SELECT t.* from Topic t " +
                        "join Command team on team.id = t.team_id " +
                        "where t.publicTopic = 1 and t.mainTopic = 0 and t.lastUpdateDate IS NOT NULL and team.country_id = ? " +
                        "and (select count(*) from UserBlacklistTeam ubt where ubt.User_id = ? and ubt.Team_id = t.team_id) = 0 " +
                        "ORDER BY t.lastUpdateDate DESC LIMIT ? OFFSET ?";

                topics = JPA.em().createNativeQuery(query, Topic.class)
                        .setParameter(1, country)
                        .setParameter(2, user.id)
                        .setParameter(3, limit)
                        .setParameter(4, offset)
                        .getResultList();

            } else {
                final String categoryTableName;
                if(category.equals(TRACKED_TOPIC_CATEGORY_BLACKLIST)){
                    categoryTableName = "UserBlacklistTeam";
                } else {
                    categoryTableName = "UserFavouriteTeam";
                }

                // 3. CATEGORY: select all PUBLIC topics of ALL teams in chosen CATEGORY
                if(country == null) {

                    query = "SELECT t.* from Topic t " +
                            "join " + categoryTableName + " cat on (cat.User_id = ? and cat.Team_id = t.team_id) " +
                            "where t.publicTopic = 1 and t.mainTopic = 0 and t.lastUpdateDate IS NOT NULL " +
                            "ORDER BY t.lastUpdateDate DESC LIMIT ? OFFSET ?";

                    topics = JPA.em().createNativeQuery(query, Topic.class)
                            .setParameter(1, user.id)
                            .setParameter(2, limit)
                            .setParameter(3, offset)
                            .getResultList();

                } else {
                    // 4. COUNTRY & CATEGORY: select all PUBLIC topics of ALL teams in chosen COUNTRY & CATEGORY

                    query = "SELECT t.* from Topic t " +
                            "join Command team on team.id = t.team_id " +
                            "join " + categoryTableName + " cat on (cat.User_id = ? and cat.Team_id = t.team_id) " +
                            "where t.publicTopic = 1 and t.mainTopic = 0 and t.lastUpdateDate IS NOT NULL and team.country_id = ? " +
                            "ORDER BY t.lastUpdateDate DESC LIMIT ? OFFSET ?";

                    topics = JPA.em().createNativeQuery(query, Topic.class)
                            .setParameter(1, user.id)
                            .setParameter(2, country)
                            .setParameter(3, limit)
                            .setParameter(4, offset)
                            .getResultList();
                }
            }

        }

        return topics;
    }


	public static void editSkill(UserSkillDTO currentUser) {
        checkAuthenticity();
		User user = User.findById(SessionHelper.getCurrentUser(session).getId());

        if(currentUser == null) {
            index(user.id);
        }

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
        checkAuthenticity();
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

            TeamMemberActivity.log(userCurrent, TeamMemberActivity.Action.ACTION_MEMBER_APPLIED, group);

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
                TeamMemberActivity.log(userCurrent, TeamMemberActivity.Action.ACTION_MEMBER_REFUSED_TO_JOIN, group);

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
            TeamMemberActivity.log(user, TeamMemberActivity.Action.ACTION_MEMBER_JOINED, group);
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
            TeamMemberActivity.log(user, TeamMemberActivity.Action.ACTION_MEMBER_REFUSED_TO_JOIN, group);
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

        TeamMemberActivity.log(user, TeamMemberActivity.Action.ACTION_MEMBER_LEFT, user.command, null, null);

		user.command = null;
		user.save();
		SessionHelper.setCurrentUser(session, user);
		index(user.id);
	}


    public static void addTeamToFavourites(Long teamId/*, Integer country, Integer category*/) {
        User user = User.findById(SessionHelper.getCurrentUser(session).id);
        Command team = Command.findById(teamId);
        if(team != null) {
            user.favouriteTeams.add(team);
            user.save();
            SessionHelper.setCurrentUser(session, user);
        }
        //UserController.teamtrack(country, category);
        renderJSON("{\"status\": \"Ok\"}");
    }

    public static void addTeamToBlacklist(Long teamId) {
        User user = User.findById(SessionHelper.getCurrentUser(session).id);
        Command team = Command.findById(teamId);
        if(team != null) {
            user.blacklistTeams.add(team);
            user.save();
            SessionHelper.setCurrentUser(session, user);
        }

        renderJSON("{\"status\": \"Ok\"}");
    }

    public static void removeTeamFromFavourites(Long teamId) {
        User user = User.findById(SessionHelper.getCurrentUser(session).id);
        Command team = Command.findById(teamId);
        if(team != null) {
            user.favouriteTeams.remove(team);
            user.save();
            SessionHelper.setCurrentUser(session, user);
        }

        renderJSON("{\"status\": \"Ok\"}");
    }

    public static void removeTeamFromBlacklist(Long teamId) {
        User user = User.findById(SessionHelper.getCurrentUser(session).id);
        Command team = Command.findById(teamId);
        if(team != null) {
            user.blacklistTeams.remove(team);
            user.save();
            SessionHelper.setCurrentUser(session, user);
        }

        renderJSON("{\"status\": \"Ok\"}");
    }

}