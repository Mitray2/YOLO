package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import modelDTO.GroupSearchDTO;
import modelDTO.MemberSearchDTO;
import models.Command;
import models.User;

import org.apache.commons.lang.StringUtils;

import com.google.gson.GsonBuilder;

import controllers.UsersSearch.UserSearchAjaxResult;

import play.modules.paginate.ValuePaginator;
import utils.ApplicationConstants;

public class CommandsSearch extends AbstractSearch {
	
	public static void groupSearchAjax() {
		GroupSearchDTO group = new GsonBuilder().create().fromJson(request.params.get("body"), GroupSearchDTO.class);
		
		Integer currentPage = Integer.valueOf(request.params.get("page"));
		
		statement = "select distinct c from Command c left join c.country as cc " + "left join c.type as type " + "left join c.sphere as s " + "left join c.marketing as m "
				+ "left join m.level as marl " + "left join c.management as man " + "left join man.level as manl " + "left join c.trade as t " + "left join t.level as tl "
				+ "left join c.finance as f " + "left join f.level as fl " + "left join c.legal as l " + "left join l.level as ll " + "left join c.programming as pr "
				+ "left join pr.level as prl ";
		
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
			appendParam(group.marketing, "marl.userLevel", where, EQUAL, queryParams);
			appendParam(group.sale, "tl.userLevel", where, EQUAL, queryParams);
			appendParam(group.management, "manl.userLevel", where, EQUAL, queryParams);
			appendParam(group.finance, "fl.userLevel", where, EQUAL, queryParams);
			appendParam(group.legal, "ll.userLevel", where, EQUAL, queryParams);
			appendParam(group.it, "prl.userLevel", where, EQUAL, queryParams);
			appendParam(group.bmanMin, "c.businessman", where, MORE, queryParams);
			appendParam(group.bmanMax, "c.businessman", where, LESS, queryParams);
			appendParam(group.idealMin, "c.idealist", where, MORE, queryParams);
			appendParam(group.idealMax, "c.idealist", where, LESS, queryParams);
			appendParam(group.comutMin, "c.communicant", where, MORE, queryParams);
			appendParam(group.comutMax, "c.communicant", where, LESS, queryParams);
			appendParam(group.pragmatMin, "c.pragmatist", where, MORE, queryParams);
			appendParam(group.pragmatMax, "c.pragmatist", where, LESS, queryParams);
			statement += where.toString();
		}
		StringBuilder orderBy = new StringBuilder();

		if (group != null) {
			if (group.orderBy != null) {
				orderBy.append(" ORDER BY ");
				orderBy.append("c.");
				orderBy.append(group.orderBy);
				if (group.asc) {
					orderBy.append(" asc");
				} else {
					orderBy.append(" desc");
				}
			}
		}
		statement += orderBy.toString();
		List<Command> groups = Command.find(statement, queryParams.toArray()).fetch(currentPage, ApplicationConstants.SEARCH_COUNT_ON_PAGE);

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (Command command : groups) {
			if (command.country != null) {
				System.out.println("\n\n\n\n\n\n" + command.id);
				Map<String, Object> group_search = new HashMap<String, Object>();

				group_search.put("id", command.id);
				group_search.put("name", command.name);
				group_search.put("country", command.country.name);
				group_search.put("city", command.city);
				group_search.put("count", command.countUser);
				group_search.put("age", command.middleAge);
				group_search.put("haveAvatar", false);
				group_search.put("info", command.description);
				
				group_search.put("businessman", command.businessman);
				group_search.put("idealist", command.idealist);
				group_search.put("communicant", command.communicant);
				group_search.put("pragmatist", command.pragmatist);
				
				group_search.put("businessType", command.type.name);
				group_search.put("businessSphere", command.sphere.name);
				group_search.put("international", "Yes"); // TODO not
															// approved
				group_search.put("status", "Online");// TODO not
				// approved
				
				group_search.put("marketing", command.marketing.level != null ? command.marketing.level.id : null);
				group_search.put("sale", command.trade.level != null ? command.trade.level.id : null);
				group_search.put("management", command.management.level != null ? command.management.level.id : null);
				group_search.put("finance", command.finance.level != null ? command.finance.level.id : null);
				group_search.put("legal", command.legal.level != null ? command.legal.level.id : null);
				group_search.put("it", command.programming.level != null ? command.programming.level.id : null);
				group_search.put("other", command.otherSkill.level != null ? command.otherSkill.level.id : null);
				
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
		
		sortOrders.put("country", "cs.name");
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


		sortOrders.put("marketing", "marl.userLevel");
		sortOrders.put("sale", "tl.userLevel");
		sortOrders.put("management", "manl.userLevel");
		sortOrders.put("finance", "fl.userLevel");
		sortOrders.put("right", "ll.userLevel");
		sortOrders.put("it", "prl.userLevel");
		sortOrders.put("more", "otherl.userLevel");
		sortOrders.put("command", "u.command");
	}
	
	public static class GroupsSearchAjaxResult {
		public List<Map<String, Object>> groups;
		public Integer pagesCount; 
	}
}
