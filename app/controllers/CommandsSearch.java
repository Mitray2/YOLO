package controllers;

import java.util.ArrayList;
import java.util.List;

import modelDTO.GroupSearchDTO;
import models.Command;

import org.apache.commons.lang.StringUtils;

import play.modules.paginate.ValuePaginator;

public class CommandsSearch extends AbstractSearch {

	public static void groupSearch(GroupSearchDTO group) {

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
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" + statement);
		List<Command> groups = Command.find(statement, queryParams.toArray()).fetch();
		ValuePaginator paginator = new ValuePaginator(groups);
		paginator.setPageSize(ITEMS_PER_PAGE);

		render(paginator, group);
	}
}
