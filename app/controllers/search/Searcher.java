package controllers.search;

import org.apache.commons.lang.StringUtils;
import play.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Searcher {

    public static enum Operation {
        EQUAL(" = ", false),
        NOT_EQUAL(" <> ", false),
        LIKE(" like ", false),
        MORE (" >= ", false),
        LESS(" <= ", false),
        IN(" IN ", false),
        IS_NOT_NULL(" IS NOT NULL ", true),
        IS_NULL(" IS NULL ", true);

        private String op;
        private boolean unary;

        private Operation(String sqlOperation, boolean unary) {
            this.op = sqlOperation;
            this.unary = unary;
        }

        public String getOp() {
            return op;
        }

        public boolean isUnary() {
            return unary;
        }
    }

	public static int ITEMS_PER_PAGE = 5;

    private String statement;
    private List<Object> queryParams = new ArrayList<Object>();
    private StringBuilder where = new StringBuilder();
    private StringBuilder orderBy = new StringBuilder();
    private Boolean orderBySet = false;
	public static Map<String, String> sortOrders = new HashMap<String, String>();


	public void addParam(String name, Operation op, Object value) {
        if (!op.unary && (value == null || StringUtils.isEmpty(value.toString()))) {
            return;
        }

        if (where.length() != 0) {
            where.append(" and ");
        } else {
            where.append(" where ");
        }

        where.append(name).append(op.op);

        if(op == Operation.IN) {
            try{
                List params = (List) value;
                where.append(" (").append(
                    StringUtils.repeat("?,", params.size()).replaceAll(",$", "")
                ).append(")");

                for (Object param : params) {
                    queryParams.add(param);
                }
            } catch(Exception e) {
                Logger.error("value for IN operation is not an instance of list");
            }
        } else if(!op.unary) {
            where.append(" ? ");
            queryParams.add(value);
        }
	}


	public void setOrder(String param, Boolean asc) {
        if(!orderBySet){
            orderBy.append(" ORDER BY ");
            orderBy.append(param);
            if (asc) {
                orderBy.append(" ASC");
            } else {
                orderBy.append(" DESC");
            }
            orderBySet = true;
        }
	}


    public String getStatement() {
        return statement;
    }

    public String getFullQuery() {
        return statement + where.toString() + orderBy.toString();
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public List<Object> getQueryParams() {
        return queryParams;
    }

    public String getWhereClause() {
        return where.toString();
    }

    public void reset(){
        this.statement = "";
        this.queryParams = new ArrayList<Object>();
        this.where = new StringBuilder();
        this.orderBy = new StringBuilder();
        orderBySet = false;
    }



}
