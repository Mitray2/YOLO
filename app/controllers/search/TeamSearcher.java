package controllers.search;

import java.util.HashMap;
import java.util.Map;

public class TeamSearcher extends Searcher {

    public static final Map<String, String> sortOrders = new HashMap<String, String>();
    static {
        sortOrders.put("search", "group");

        sortOrders.put("country", "cc.name");
        sortOrders.put("city", "c.city");
        sortOrders.put("name", "c.name");
        sortOrders.put("age", "c.middleAge");
        sortOrders.put("global", "c.global");

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


}
