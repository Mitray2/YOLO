package controllers;

import com.google.gson.GsonBuilder;
import controllers.search.Searcher;
import controllers.search.TeamSearcher;
import modelDTO.GroupSearchDTO;
import models.Command;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import play.i18n.Messages;
import play.mvc.Controller;
import utils.ApplicationConstants;
import utils.DateUtils;
import utils.SessionHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static controllers.search.Searcher.Operation.*;

public class CommandsSearch extends Controller {
	
	public static void groupSearchAjax() {
        final Searcher searcher = new TeamSearcher();

		GroupSearchDTO group = new GsonBuilder().create().fromJson(request.params.get("body"), GroupSearchDTO.class);
		
		
		searcher.setStatement("select distinct c from Command c left join c.country as cc " + "left join c.type as type " + "left join c.sphere as s " + "left join c.marketing as m "
				+ "left join c.management as man " + "left join c.trade as t "
				+ "left join c.finance as f " + "left join c.legal as l " + "left join c.programming as pr " + "left join c.otherSkill as other "
				+ "left join c.phase as phs ");

		if (group != null) {

			if (StringUtils.isNotEmpty(group.vacancy)) {
                searcher.addParam("c.isVacancy", EQUAL, group.vacancy.equals("true"));
			}
			searcher.addParam("c.middleAge", MORE, group.avgAgeMin);
			searcher.addParam("c.middleAge", LESS, group.avgAgeMax);
			searcher.addParam("c.countUser", MORE, group.usersMin);
			searcher.addParam("c.countUser", LESS, group.usersMax);
			searcher.addParam("cc.name", EQUAL, group.country);
			searcher.addParam("c.city", LIKE, group.city + "%");
			searcher.addParam("type.name", EQUAL, group.bissnessType);
			searcher.addParam("s.name", EQUAL, group.bissnessSphere);
			if (StringUtils.isNotEmpty(group.marketing)) {
				searcher.addParam("m.active", EQUAL, BooleanUtils.toBoolean(group.marketing));
			}
			if (StringUtils.isNotEmpty(group.sale)) {
				searcher.addParam("t.active", EQUAL, BooleanUtils.toBoolean(group.sale));
			}
			if (StringUtils.isNotEmpty(group.management)) {
				searcher.addParam("man.active", EQUAL, BooleanUtils.toBoolean(group.management));
			}
			if (StringUtils.isNotEmpty(group.finance)) {
				searcher.addParam("f.active", EQUAL, BooleanUtils.toBoolean(group.finance));
			}
			if (StringUtils.isNotEmpty(group.legal)) {
				searcher.addParam("l.active", EQUAL, BooleanUtils.toBoolean(group.legal));
			}
			if (StringUtils.isNotEmpty(group.it)) {
				searcher.addParam("pr.active", EQUAL, BooleanUtils.toBoolean(group.it));
			}
			if (StringUtils.isNotEmpty(group.other)) {
				searcher.addParam("other.active", EQUAL, BooleanUtils.toBoolean(group.other));
			}
			if (StringUtils.isNotEmpty(group.global)) {
				searcher.addParam("c.global", EQUAL, BooleanUtils.toBoolean(group.global));
			}
			searcher.addParam("c.businessman", MORE, group.bmanMin);
			searcher.addParam("c.businessman", LESS, group.bmanMax);
			searcher.addParam("c.idealist", MORE, group.idealMin);
			searcher.addParam("c.idealist", LESS, group.idealMax);
			searcher.addParam("c.communicant", MORE, group.comutMin);
			searcher.addParam("c.communicant", LESS, group.comutMax);
			searcher.addParam("c.pragmatist", MORE, group.pragmatMin);
			searcher.addParam("c.pragmatist", LESS, group.pragmatMax);
			searcher.addParam("phs.name", EQUAL, group.phase);
		}

		if (group != null && group.orderBy != null) {
            searcher.setOrder(TeamSearcher.sortOrders.get(group.orderBy), group.asc);
		} else {
            searcher.setOrder(TeamSearcher.sortOrders.get("lastSeen"), false);
        }

        Integer currentPage = Integer.valueOf(request.params.get("page"));
		List<Command> groups = Command.find(searcher.getFullQuery(), searcher.getQueryParams().toArray())
                                      .fetch(currentPage, ApplicationConstants.SEARCH_COUNT_ON_PAGE);

		List<Map<String, Object>> foundTeams = new ArrayList<Map<String, Object>>();
		for (Command command : groups) {
			if (command.country != null) {
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
				group_search.put("global", (command.global != null)  ? (command.global == true ? Messages.get("common.yes") : Messages.get("common.no")) : Messages.get("common.no")); 
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
				
				foundTeams.add(group_search);
			}

		}
		GroupsSearchAjaxResult result = new GroupsSearchAjaxResult();
		result.groups = foundTeams;
		if (groups.isEmpty()){
			result.pagesCount = currentPage;
		}
		else{
			result.pagesCount = currentPage + 1;
		}

		renderJSON(result);
	}
	
	public static void groupSearch() {
        if(SessionHelper.getCurrentUser(session) == null) {
            ApplicationController.index();
        }
		render();
	}

	public static class GroupsSearchAjaxResult {
		public List<Map<String, Object>> groups;
		public Integer pagesCount; 
	}
}
