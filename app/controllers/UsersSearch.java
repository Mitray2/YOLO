package controllers;

import com.google.gson.GsonBuilder;
import controllers.search.Searcher;
import controllers.search.UserSearcher;
import modelDTO.FriendSearchDTO;
import modelDTO.MemberSearchDTO;
import models.User;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import play.i18n.Messages;
import play.modules.paginate.ValuePaginator;
import play.mvc.Before;
import play.mvc.Controller;
import utils.ApplicationConstants;
import utils.DateUtils;
import utils.SessionHelper;

import java.util.*;

import static controllers.search.Searcher.ITEMS_PER_PAGE;
import static controllers.search.Searcher.Operation.*;

public class UsersSearch extends Controller {

    //private static final Logger log = play.Logger.log4j;

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
        }
        if (currentUser.role == User.ROLE_INPERFECT_USER) {
            redirect(request.getBase() + ApplicationConstants.BLANK_FORM_PATH);
        }
        if (currentUser.role.equals(User.ROLE_WITHOUT_BLANK)) {
            redirect(request.getBase() + ApplicationConstants.BLANK_FORM_PATH);
        }

    }

    public static void memberSearchAjax() {
        final Searcher searcher = new UserSearcher();

        MemberSearchDTO member = new GsonBuilder().create().fromJson(request.params.get("body"), MemberSearchDTO.class);

        searcher.setStatement("select distinct u from User u left join u.country as c " + "left join u.businessType as type " + "left join u.businessSphere as s "
                + "left join u.expMarketing as m " + "left join m.level as marl " + "left join u.expManagement as man " + "left join man.level as manl "
                + "left join u.expSale as t " + "left join t.level as tl " + "left join u.expFinance as f " + "left join f.level as fl " + "left join u.expLegal as l "
                + "left join l.level as ll " + "left join u.expIT as pr " + "left join pr.level as prl " + "left join u.expOther as other " + "left join other.level as otherl ");

        if (member != null) {
            searcher.addParam("c.name", EQUAL, member.country);
            searcher.addParam("u.city", LIKE, member.city + "%");
            searcher.addParam("u.age", MORE, member.ageMin);
            searcher.addParam("u.age", LESS, member.ageMax);
            searcher.addParam("u.businessman", MORE, member.bmanMin);
            searcher.addParam("u.businessman", LESS, member.bmanMax);
            searcher.addParam("u.idealist", MORE, member.idealMin);
            searcher.addParam("u.idealist", LESS, member.idealMax);
            searcher.addParam("u.communicant", MORE, member.comutMin);
            searcher.addParam("u.communicant", LESS, member.comutMax);
            searcher.addParam("u.pragmatist", MORE, member.pragmatMin);
            searcher.addParam("u.pragmatist", LESS, member.pragmatMax);
            searcher.addParam("type.name", EQUAL, member.bissnessType);
            searcher.addParam("s.name", EQUAL, member.bissnessSphere);
            searcher.addParam("marl.userLevel", EQUAL, member.marketing);
            searcher.addParam("tl.userLevel", EQUAL, member.sale);
            searcher.addParam("manl.userLevel", EQUAL, member.management);
            searcher.addParam("fl.userLevel", EQUAL, member.finance);
            searcher.addParam("ll.userLevel", EQUAL, member.legal);
            searcher.addParam("prl.userLevel", EQUAL, member.it);

            if (StringUtils.isNotEmpty(member.sex)) {
                searcher.addParam("u.sex", EQUAL, BooleanUtils.toBoolean(member.sex));
            }

            if (StringUtils.isNotEmpty(member.english)) {
                searcher.addParam("u.english", EQUAL, BooleanUtils.toBoolean(member.english));
            }

            if (member.inCommand != null) {
                if (member.inCommand.equals("true")) {
                    searcher.addParam("u.command", IS_NOT_NULL, null);
                }
                if (member.inCommand.equals("false")) {
                    searcher.addParam("u.command", IS_NULL, null);
                }
            }
        }

        searcher.addParam("u.role", IN, Arrays.asList(User.ROLE_USER, User.ROLE_GROUP_ADMIN));

        if (member != null && member.orderBy != null) {
            searcher.setOrder(UserSearcher.sortOrders.get(member.orderBy), member.asc);
        } else {
            searcher.setOrder(UserSearcher.sortOrders.get("lastSeen"), false);
        }

        final Integer currentPage = Integer.valueOf(request.params.get("page"));
        //Logger.info(searcher.getFullQuery());
        List<User> usersOriginal = User.find(searcher.getFullQuery(), searcher.getQueryParams().toArray())
                                       .fetch(currentPage, ApplicationConstants.SEARCH_COUNT_ON_PAGE);
        List<Map<String, Object>> foundUsers = new ArrayList<Map<String, Object>>();
        for (User user : usersOriginal) {
            if (user.country != null) {
                Map<String, Object> user_search = new HashMap<String, Object>();

                user_search.put("lastSeen", DateUtils.getFormatedStringDate(user.lastSeen, true));
                user_search.put("onlineStatus", user.onlineStatus);
                user_search.put("id", user.id);
                user_search.put("lastName", user.lastName);
                user_search.put("name", user.name);
                user_search.put("country", Messages.get(ApplicationConstants.MESSAGES_COUNTRY_NAME + utils.ModelUtils.replaceSpacesForI18n(user.country.name)));
                user_search.put("city", user.city);
                user_search.put("sex", user.sex);
                user_search.put("age", user.age);
                user_search.put("haveAvatar", user.haveAvatar);
                user_search.put("info", user.personalCV);
                user_search.put("commandB", user.command != null);
                user_search.put("commandId", user.command == null ? false : user.command.id);

                user_search.put("commandName", user.command == null ? false : user.command.name);

                user_search.put("businessman", user.businessman);
                user_search.put("idealist", user.idealist);
                user_search.put("communicant", user.communicant);
                user_search.put("pragmatist", user.pragmatist);

                user_search.put("businessType", Messages.get(ApplicationConstants.MESSAGES_BUSINESS_TYPE + utils.ModelUtils.replaceSpacesForI18n(user.businessType.name)));
                user_search.put("businessSphere", Messages.get(ApplicationConstants.MESSAGES_SPHERE_NAME + utils.ModelUtils.replaceSpacesForI18n(user.businessSphere.name)));
                user_search.put("english", user.english ? Messages.get("common.yes") : Messages.get("common.no"));

                user_search.put("marketing", user.expMarketing.level.id);
                user_search.put("marketingInfo", user.expMarketing.description == null ? "" : user.expMarketing.description);
                user_search.put("sale", user.expSale.level.id);
                user_search.put("saleInfo", user.expSale.description == null ? "" : user.expSale.description);
                user_search.put("management", user.expManagement.level.id);
                user_search.put("managementInfo", user.expManagement.description == null ? "" : user.expManagement.description);
                user_search.put("finance", user.expFinance.level.id);
                user_search.put("financeInfo", user.expFinance.description == null ? "" : user.expFinance.description);
                user_search.put("legal", user.expLegal.level.id);
                user_search.put("legalInfo", user.expLegal.description == null ? "" : user.expLegal.description);
                user_search.put("it",user.expIT.level.id);
                user_search.put("itInfo", user.expIT.description == null ? "" : user.expIT.description);
                user_search.put("other", user.expOther.level.id);
                user_search.put("otherInfo", user.expOther.description == null ? "" : user.expOther.description);

                foundUsers.add(user_search);
            }

        }
        UserSearchAjaxResult result = new UserSearchAjaxResult();
        result.users = foundUsers;
        if (usersOriginal.isEmpty()){
            result.pagesCount = currentPage;
        }
        else{
            result.pagesCount = currentPage + 1;
        }

        renderJSON(result);
    }

    public static void memberSearch(MemberSearchDTO member) {
        if(SessionHelper.getCurrentUser(session) == null) {
            ApplicationController.index();
        }

        render();
    }

    public static void peopleSearch(FriendSearchDTO friend) {
        final Searcher searcher = new UserSearcher();

        searcher.setStatement("select distinct u from User u left join u.country as c ");

        if (friend != null) {
            String name = friend.name + "%";
            String lastName = friend.lastName + "%";
            String city = friend.city + "%";
            searcher.addParam("u.name", LIKE, name);
            searcher.addParam("u.lastName", LIKE, lastName);
            searcher.addParam("c.name", EQUAL, friend.country);
            searcher.addParam("u.city", LIKE, city);
            searcher.addParam("u.age", MORE, friend.min);
            searcher.addParam("u.age", LESS, friend.max);
            searcher.addParam("u.email", EQUAL, friend.email);
            if (StringUtils.isNotEmpty(friend.sex)) {
                searcher.addParam("u.sex", EQUAL, friend.sex.equals("true"));
            }
        }

        searcher.addParam("u.role", IN, Arrays.asList(User.ROLE_USER, User.ROLE_GROUP_ADMIN));

        List<User> users = User.find(searcher.getFullQuery(), searcher.getQueryParams().toArray()).fetch();
        ValuePaginator paginator = new ValuePaginator(users);
        paginator.setPageSize(ITEMS_PER_PAGE);

        render(paginator, friend);
    }

    public static class UserSearchAjaxResult {
        public List<Map<String, Object>> users;
        public Integer pagesCount;
    }
}
