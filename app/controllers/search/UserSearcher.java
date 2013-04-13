package controllers.search;

import java.util.HashMap;
import java.util.Map;

public class UserSearcher extends Searcher {

    public static final Map<String, String> sortOrders = new HashMap<String, String>();
    static {
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
        sortOrders.put("english", "u.english");

        sortOrders.put("marketing", "marl.id");
        sortOrders.put("sale", "tl.id");
        sortOrders.put("management", "manl.id");
        sortOrders.put("finance", "fl.id");
        sortOrders.put("right", "ll.id");
        sortOrders.put("it", "prl.id");
        sortOrders.put("more", "otherl.id");
        sortOrders.put("command", "u.command");
        sortOrders.put("lastSeen", "u.lastSeen");
    }


}
