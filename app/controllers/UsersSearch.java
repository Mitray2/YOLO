package controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import modelDTO.FriendSearchDTO;
import modelDTO.MemberSearchDTO;
import models.LastUserData;
import models.User;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import play.i18n.Messages;
import play.modules.paginate.ValuePaginator;
import play.mvc.Before;
import utils.ApplicationConstants;
import utils.DateUtils;
import utils.SessionHelper;

import com.google.gson.GsonBuilder;

public class UsersSearch extends AbstractSearch {


	

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

	public static void memberSearchAjax() {
		MemberSearchDTO member = new GsonBuilder().create().fromJson(request.params.get("body"), MemberSearchDTO.class);
		
		Integer currentPage = Integer.valueOf(request.params.get("page"));
		
		statement = "select distinct u from User u left join u.country as c " + "left join u.businessType as type " + "left join u.businessSphere as s "
				+ "left join u.expMarketing as m " + "left join m.level as marl " + "left join u.expManagement as man " + "left join man.level as manl "
				+ "left join u.expSale as t " + "left join t.level as tl " + "left join u.expFinance as f " + "left join f.level as fl " + "left join u.expLegal as l "
				+ "left join l.level as ll " + "left join u.expIT as pr " + "left join pr.level as prl " + "left join u.expOther as other " + "left join other.level as otherl ";
		queryParams = new ArrayList<Object>();
		where = new StringBuilder();
		if (member != null) {

			String city = member.city + "%";
			appendParam(member.country, "c.name", where, EQUAL, queryParams);
			appendParam(city, "u.city", where, LIKE, queryParams);
			appendParam(member.ageMin, "u.age", where, MORE, queryParams);
			appendParam(member.ageMax, "u.age", where, LESS, queryParams);
			appendParam(member.bmanMin, "u.businessman", where, MORE, queryParams);
			appendParam(member.bmanMax, "u.businessman", where, LESS, queryParams);
			appendParam(member.idealMin, "u.idealist", where, MORE, queryParams);
			appendParam(member.idealMax, "u.idealist", where, LESS, queryParams);
			appendParam(member.comutMin, "u.communicant", where, MORE, queryParams);
			appendParam(member.comutMax, "u.communicant", where, LESS, queryParams);
			appendParam(member.pragmatMin, "u.pragmatist", where, MORE, queryParams);
			appendParam(member.pragmatMax, "u.pragmatist", where, LESS, queryParams);
			appendParam(member.bissnessType, "type.name", where, EQUAL, queryParams);
			appendParam(member.bissnessSphere, "s.name", where, EQUAL, queryParams);
			appendParam(member.marketing, "marl.userLevel", where, EQUAL, queryParams);
			appendParam(member.sale, "tl.userLevel", where, EQUAL, queryParams);
			appendParam(member.management, "manl.userLevel", where, EQUAL, queryParams);
			appendParam(member.finance, "fl.userLevel", where, EQUAL, queryParams);
			appendParam(member.legal, "ll.userLevel", where, EQUAL, queryParams);
			appendParam(member.it, "prl.userLevel", where, EQUAL, queryParams);
			
			
			if (StringUtils.isNotEmpty(member.sex)) {
				appendParam(BooleanUtils.toBoolean(member.sex), "u.sex", where, EQUAL, queryParams);
			}
			
			if (member.inCommand != null) {
				if (member.inCommand.equals("true")) {
					where.append(" and ");
					where.append("u.command").append(IS_NOT_NULL);
				}
				if (member.inCommand.equals("false")) {
					where.append(" and ");
					where.append("u.command").append(IS_NULL);
				}
			}

		}
		if (where.length() == 0) {
			where.append(" where u.role IN (?,?)");
			queryParams.add(User.ROLE_USER);
			queryParams.add(User.ROLE_GROUP_ADMIN);
		} else {
			where.append(" and u.role IN (?,?)");
			queryParams.add(User.ROLE_USER);
			queryParams.add(User.ROLE_GROUP_ADMIN);
		}
		statement += where.toString();

		StringBuilder orderBy = new StringBuilder();

		if (member != null) {
			if (member.orderBy != null) {
				orderBy.append(" ORDER BY ");
				orderBy.append(sortOrders.get(member.orderBy));
				if (member.asc) {
					orderBy.append(" asc");
				} else {
					orderBy.append(" desc");
				}
			}
		}
		statement += orderBy.toString();
		statement += "";
		List<User> usersOriginal = User.find(statement, queryParams.toArray()).fetch(currentPage, ApplicationConstants.SEARCH_COUNT_ON_PAGE);
		// List<User> usersOriginal = User.findAll();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (User user : usersOriginal) {
			if (user.country != null) {
				System.out.println("\n\n\n\n\n\n" + user.id);
				Map<String, Object> user_search = new HashMap<String, Object>();

				LastUserData lastSeenUserData = null;
				String statement = "select l from LastUserData as l where l.userId=?";
				List<LastUserData> lastSeenUserDatas = LastUserData.find(statement, user.id).fetch(1);
				
				lastSeenUserData = LastUserData.findById(lastSeenUserDatas.get(0).id);
				
				
				
				user_search.put("lastSeen", DateUtils.getFormatedStringDate(lastSeenUserData.lastSeen, true));
				user_search.put("id", user.id);
				user_search.put("lastName", user.lastName);
				user_search.put("name", user.name);
				user_search.put("country", Messages.get(ApplicationConstants.MESSAGES_COUNTRY_NAME + utils.ModelUtils.replaceSpacesForI18n(user.country.name)));
				user_search.put("city", user.city);
				user_search.put("sex", user.sex);
				user_search.put("age", user.age);
				user_search.put("haveAvatar", user.haveAvatar);
				user_search.put("info", user.personalCV);
				user_search.put("commandB", user.command == null ? false : true);
				user_search.put("commandId", user.command == null ? false : user.command.id);
				
				user_search.put("commandName", user.command == null ? false : user.command.name);
				
				user_search.put("businessman", user.businessman);
				user_search.put("idealist", user.idealist);
				user_search.put("communicant", user.communicant);
				user_search.put("pragmatist", user.pragmatist);
				
				user_search.put("businessType", Messages.get(ApplicationConstants.MESSAGES_BUSINESS_TYPE + utils.ModelUtils.replaceSpacesForI18n(user.businessType.name)));
				user_search.put("businessSphere", Messages.get(ApplicationConstants.MESSAGES_SPHERE_NAME + utils.ModelUtils.replaceSpacesForI18n(user.businessSphere.name)));
				user_search.put("international", "Yes"); // TODO not
															// approved
				user_search.put("status", "Online");
				
				user_search.put("marketing", user.expMarketing.level.id);
				user_search.put("marketingInfo", user.expMarketing.description);
				user_search.put("sale", user.expSale.level.id);
				user_search.put("saleInfo", user.expSale.description);
				user_search.put("management", user.expManagement.level.id);
				user_search.put("managementInfo", user.expManagement.description);
				user_search.put("finance", user.expFinance.level.id);
				user_search.put("financeInfo", user.expFinance.description);
				user_search.put("legal", user.expLegal.level.id);
				user_search.put("legalInfo", user.expLegal.description);
				user_search.put("it",user.expIT.level.id);
				user_search.put("itInfo", user.expIT.description);
				user_search.put("other", user.expOther.level.id);
				user_search.put("otherInfo", user.expOther.description);
				
				list.add(user_search);
			}

		}
		UserSearchAjaxResult result = new UserSearchAjaxResult();
		result.users = list;
		if (usersOriginal.isEmpty()){
			result.pagesCount = currentPage;
		}
		else{
			result.pagesCount = currentPage + 1;
		}
		renderJSON(result);
	}

	public static void memberSearch(MemberSearchDTO member) {
		if (!sortOrders.isEmpty()){
			String search = sortOrders.get("search");
			if (!search.equals("members")){
				sortOrders.clear();
				addsortOrder();
			}
		}
		else{
			addsortOrder();
		}
		
		
		render();
	}
	
	protected static void addsortOrder(){
		sortOrders.put("search", "members");
		
		sortOrders.put("country", "c.name");
		sortOrders.put("city", "u.city");
		sortOrders.put("lastName", "u.lastName");
		sortOrders.put("sex", "u.sex");
		sortOrders.put("age", "u.age");
		
		
		sortOrders.put("predpr", "u.businessman");
		sortOrders.put("ideal", "u.idealist");
		sortOrders.put("communic", "u.communicant");
		sortOrders.put("pragmatic", "u.pragmatist");
		sortOrders.put("businessType", "type.name");
		sortOrders.put("businessSphere", "s.name");


		sortOrders.put("marketing", "marl.id");
		sortOrders.put("sale", "tl.id");
		sortOrders.put("management", "manl.id");
		sortOrders.put("finance", "fl.id");
		sortOrders.put("right", "ll.id");
		sortOrders.put("it", "prl.id");
		sortOrders.put("more", "otherl.id");
		sortOrders.put("command", "u.command");
	}

	public static void peopleSearch(FriendSearchDTO friend) {

		String statement = "select distinct u from User u left join u.country as c";
		queryParams = new ArrayList<Object>();
		where = new StringBuilder();
		if (friend != null) {
			String name = friend.name + "%";
			String lastName = friend.lastName + "%";
			String city = friend.city + "%";
			appendParam(name, "u.name", where, LIKE, queryParams);
			appendParam(lastName, "u.lastName", where, LIKE, queryParams);
			appendParam(friend.country, "c.name", where, EQUAL, queryParams);
			appendParam(city, "u.city", where, LIKE, queryParams);
			appendParam(friend.min, "u.age", where, MORE, queryParams);
			appendParam(friend.max, "u.age", where, LESS, queryParams);
			appendParam(friend.email, "u.email", where, EQUAL, queryParams);
			if (StringUtils.isNotEmpty(friend.sex)) {
				if (friend.sex.equals("true")) {
					appendParam(true, "u.sex", where, EQUAL, queryParams);
				} else {
					appendParam(false, "u.sex", where, EQUAL, queryParams);
				}
			}
		}
		if (where.length() == 0) {
			where.append(" where u.role IN (?,?)");
			queryParams.add(User.ROLE_USER);
			queryParams.add(User.ROLE_GROUP_ADMIN);
		} else {
			where.append(" and u.role IN (?,?)");
			queryParams.add(User.ROLE_USER);
			queryParams.add(User.ROLE_GROUP_ADMIN);
		}
		statement += where.toString();
		List<User> users = User.find(statement, queryParams.toArray()).fetch();
		ValuePaginator paginator = new ValuePaginator(users);
		paginator.setPageSize(ITEMS_PER_PAGE);

		render(paginator, friend);
	}
	
	public static class UserSearchAjaxResult {
		public List<Map<String, Object>> users;
		public Integer pagesCount; 
	}
}
