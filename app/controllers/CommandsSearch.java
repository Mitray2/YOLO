package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import modelDTO.GroupSearchDTO;
import modelDTO.MemberSearchDTO;
import models.Command;
import models.User;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import com.google.gson.GsonBuilder;

import controllers.UsersSearch.UserSearchAjaxResult;

import play.i18n.Messages;
import play.modules.paginate.ValuePaginator;
import utils.ApplicationConstants;
import utils.DateUtils;

public class CommandsSearch extends AbstractSearch {
	
	public static void groupSearchAjax() {
		GroupSearchDTO group = new GsonBuilder().create().fromJson(request.params.get("body"), GroupSearchDTO.class);
		Integer currentPage = Integer.valueOf(request.params.get("page"));
		
		statement = "select distinct c from Command c left join c.country as cc " + "left join c.type as type " + "left join c.sphere as s " + "left join c.marketing as m "
				+ "left join c.management as man " + "left join c.trade as t "
				+ "left join c.finance as f " + "left join c.legal as l " + "left join c.programming as pr " + "left join c.otherSkill as other "
				+ "left join c.phase as phs ";
		
		queryParams = new ArrayList<Object>();
		
		if (group != null) {
			where = new StringBuilder();

			if (StringUtils.isNotEmpty(group.vacancy)) {
				if (group.vacancy.equals("true")) {
					appendParam(true, "c.isVacancy", where, EQUAL, queryParams);
				} else {
					appendParam(false, "c.isVacancy", where, EQUAL, queryParams);
				}
			}
			String sCity = group.city + "%";
			appendParam(group.avgAgeMin, "c.middleAge", where, MORE, queryParams);
			appendParam(group.avgAgeMax, "c.middleAge", where, LESS, queryParams);
			appendParam(group.usersMin, "c.countUser", where, MORE, queryParams);
			appendParam(group.usersMax, "c.countUser", where, LESS, queryParams);
			appendParam(group.country, "cc.name", where, EQUAL, queryParams);
			appendParam(sCity, "c.city", where, LIKE, queryParams);
			appendParam(group.bissnessType, "type.name", where, EQUAL, queryParams);
			appendParam(group.bissnessSphere, "s.name", where, EQUAL, queryParams);
			if (StringUtils.isNotEmpty(group.marketing)) {
				appendParam(BooleanUtils.toBoolean(group.marketing), "m.active", where, EQUAL, queryParams);
			}
			if (StringUtils.isNotEmpty(group.sale)) {
				appendParam(BooleanUtils.toBoolean(group.sale), "t.active", where, EQUAL, queryParams);
			}
			if (StringUtils.isNotEmpty(group.management)) {
				appendParam(BooleanUtils.toBoolean(group.management), "man.active", where, EQUAL, queryParams);
			}
			if (StringUtils.isNotEmpty(group.finance)) {
				appendParam(BooleanUtils.toBoolean(group.finance), "f.active", where, EQUAL, queryParams);
			}
			if (StringUtils.isNotEmpty(group.legal)) {
				appendParam(BooleanUtils.toBoolean(group.legal), "l.active", where, EQUAL, queryParams);
			}
			if (StringUtils.isNotEmpty(group.it)) {
				appendParam(BooleanUtils.toBoolean(group.it), "pr.active", where, EQUAL, queryParams);
			}
			if (StringUtils.isNotEmpty(group.other)) {
				appendParam(BooleanUtils.toBoolean(group.other), "other.active", where, EQUAL, queryParams);
			}
			appendParam(group.bmanMin, "c.businessman", where, MORE, queryParams);
			appendParam(group.bmanMax, "c.businessman", where, LESS, queryParams);
			appendParam(group.idealMin, "c.idealist", where, MORE, queryParams);
			appendParam(group.idealMax, "c.idealist", where, LESS, queryParams);
			appendParam(group.comutMin, "c.communicant", where, MORE, queryParams);
			appendParam(group.comutMax, "c.communicant", where, LESS, queryParams);
			appendParam(group.pragmatMin, "c.pragmatist", where, MORE, queryParams);
			appendParam(group.pragmatMax, "c.pragmatist", where, LESS, queryParams);
			appendParam(group.phase, "phs.name", where, EQUAL, queryParams);
			statement += where.toString();
		}
		StringBuilder orderBy = new StringBuilder();

		if (group != null) {
			if (group.orderBy != null) {
				orderBy.append(" ORDER BY ");
				orderBy.append(sortOrders.get(group.orderBy));
				if (group.asc) {
					orderBy.append(" asc");
				} else {
					orderBy.append(" desc");
				}
			}
		}
		
		statement += orderBy.toString();
		System.out.println("\n\n\n\\n\n\n\n\n" + statement);
		List<Command> groups = Command.find(statement, queryParams.toArray()).fetch(currentPage, ApplicationConstants.SEARCH_COUNT_ON_PAGE);

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (Command command : groups) {
			if (command.country != null) {
				System.out.println("\n\n\n\n\n\n" + command.id);
				Map<String, Object> group_search = new HashMap<String, Object>();

				group_search.put("id", command.id);
				group_search.put("name", command.name);
				group_search.put("country", Messages.get(ApplicationConstants.MESSAGES_COUNTRY_NAME + utils.ModelUtils.replaceSpacesForI18n(command.country.name)));
				group_search.put("city", command.city);
				group_search.put("count", command.countUser);
				group_search.put("age", command.middleAge);
				group_search.put("haveAvatar", false);
				group_search.put("info", command.description);
				
				group_search.put("businessman", command.businessman);
				group_search.put("idealist", command.idealist);
				group_search.put("communicant", command.communicant);
				group_search.put("pragmatist", command.pragmatist);
				
				group_search.put("idealize", command.idealize.active);
				group_search.put("idealize_desc", command.idealize.descrition == null ? "" : command.idealize.descrition);
				group_search.put("communication", command.communication.active);
				group_search.put("communication_desc", command.communication.descrition == null ? "" : command.communication.descrition);
				group_search.put("pragmatica", command.pragmatica.active);
				group_search.put("pragmatica_desc", command.pragmatica.descrition == null ? "" : command.pragmatica.descrition);
				
				group_search.put("vacancy", command.isVacancy);
				
				group_search.put("businessType", Messages.get(ApplicationConstants.MESSAGES_BUSINESS_TYPE + utils.ModelUtils.replaceSpacesForI18n(command.type.name)));
				group_search.put("businessSphere", Messages.get(ApplicationConstants.MESSAGES_SPHERE_NAME + utils.ModelUtils.replaceSpacesForI18n(command.sphere.name)));
				group_search.put("international", "Yes"); // TODO not
															// approved
				group_search.put("lastSeen", DateUtils.getFormatedStringDate(command.lastSeen, true)); // no money
				
				group_search.put("phase", Messages.get(ApplicationConstants.MESSAGES_PROJECT_PHASE + utils.ModelUtils.replaceSpacesForI18n(command.phase.name)));
				
				group_search.put("marketing", command.marketing.active);
				group_search.put("marketing_desc", command.marketing.descrition == null ? "" : command.marketing.descrition);
				group_search.put("sale", command.trade.active);
				group_search.put("sale_desc", command.trade.descrition == null ? "" : command.trade.descrition);
				group_search.put("management", command.management.active);
				group_search.put("management_desc", command.management.descrition == null ? "" : command.management.descrition);
				group_search.put("finance", command.finance.active);
				group_search.put("finance_desc", command.finance.descrition == null ? "" : command.finance.descrition);
				group_search.put("legal", command.legal.active);
				group_search.put("legal_desc", command.legal.descrition == null ? "" : command.legal.descrition);
				group_search.put("it", command.programming.active );
				group_search.put("it_desc", command.programming.descrition == null ? "" : command.programming.descrition);
				group_search.put("other", command.otherSkill.active);
				group_search.put("other_desc", command.otherSkill.descrition == null ? "" : command.otherSkill.descrition);
				
				list.add(group_search);
			}

		}
		GroupsSearchAjaxResult result = new GroupsSearchAjaxResult();
		result.groups = list;
		if (groups.isEmpty()){
			result.pagesCount = currentPage;
		}
		else{
			result.pagesCount = currentPage + 1;
		}
		renderJSON(result);
	}
	
	public static void groupSearch() {
		if (!sortOrders.isEmpty()){
			String search = sortOrders.get("search");
			if (!search.equals("group")){
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
		sortOrders.put("search", "group");
		
		sortOrders.put("country", "cc.name");
		sortOrders.put("city", "c.city");
		sortOrders.put("name", "c.name");
		sortOrders.put("age", "c.middleAge");
		
		sortOrders.put("count", "c.countUser");
		sortOrders.put("predpr", "c.businessman");
		sortOrders.put("ideal", "c.idealist");
		sortOrders.put("communic", "c.communicant");
		sortOrders.put("pragmatic", "c.pragmatist");
		sortOrders.put("businessType", "type.name");
		sortOrders.put("businessSphere", "s.name");
		sortOrders.put("phase", "phs.name");

		sortOrders.put("marketing", "m.active");
		sortOrders.put("sale", "t.active");
		sortOrders.put("management", "man.active");
		sortOrders.put("finance", "f.active");
		sortOrders.put("right", "l.active");
		sortOrders.put("it", "pr.active");
		sortOrders.put("more", "other.active");
		sortOrders.put("lastSeen", "c.lastSeen");
	}
	
	public static class GroupsSearchAjaxResult {
		public List<Map<String, Object>> groups;
		public Integer pagesCount; 
	}
}
