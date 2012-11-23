package controllers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import play.mvc.Controller;

public abstract class AbstractSearch extends Controller {

	public static int ITEMS_PER_PAGE = 5;
	public static String EQUAL = " = ";
	public static String NOT_EQUAL = " <> ";
	public static String LIKE = " like ";
	public static String MORE = " >= ";
	public static String LESS = " <= ";
	public static String IS_NOT_NULL = " IS NOT NULL";
	public static String IS_NULL = " IS NULL";

	protected static String statement;
	protected static List<Object> queryParams = new ArrayList<Object>();
	protected static StringBuilder where = new StringBuilder();

	protected static void appendParam(Object paramValue, String paramName, StringBuilder where, String operation, List<Object> queryParams) {
		if (paramValue != null)
			if (StringUtils.isNotEmpty(paramValue.toString())) {
				if (where.length() != 0) {
					where.append(" and ");
				} else {
					where.append(" where ");
				}
				where.append(paramName).append(operation).append(" ? ");
				queryParams.add(paramValue);
			}
	}
}
