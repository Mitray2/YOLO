package controllers;

import models.CrowdConsulting;
import models.CrowdDeveloping;
import models.Post;
import models.User;
import play.Logger;
import play.db.jpa.JPA;
import play.mvc.Catch;
import utils.ApplicationConstants;
import utils.LangUtils;
import utils.SessionHelper;

import javax.persistence.Query;
import java.util.Date;
import java.util.List;

public class CrowdController extends BasicController implements ApplicationConstants {

	private static final String FUNDING_LANG = "fundingLang";

    @Catch(value = Throwable.class, priority = 1)
    public static void onError(Throwable e) {
        Logger.error(e, "[CrowdCTR] %s", e.getMessage());

        User user = User.findById(SessionHelper.getCurrentUser(session).id);
        if (user != null) {
            UserController.index(user.id);
        } else {
            ApplicationController.index();
        }
    }

	public static void funding(String lang) {
		String newsLang = LangUtils.getNewsLang(lang, request, response, FUNDING_LANG);
		Integer langId = LangUtils.langToId(newsLang);
		List<Post> fundingNews = Post.find("select p from Post p where p.type = ? and p.state = ? and lang = ? order by p.creationDate DESC", Post.TYPE_CROWD_FUNDING, Post.STATE_WORK, langId).fetch();
		render(fundingNews, newsLang);
	}
	
	public static void showFundingReport(Long id){
		Post report = Post.findById(id);
		render(report);
	}

	public static void consulting() {
		Query tQuery = JPA.em().createQuery("select t from CrowdConsulting t order by t.createDate desc");
		tQuery.setMaxResults(10);
		List<CrowdConsulting> crowdConsulting = tQuery.getResultList();
		Query tQueryCount = JPA.em().createQuery("select count(cf.id) from CrowdConsulting cf");
		Integer messagesCount = ((Long) tQueryCount.getResultList().get(0)).intValue();
		Boolean isAdmin = SessionHelper.getCurrentUser(session).role.equals(User.ROLE_ADMIN);
		render(crowdConsulting, isAdmin, messagesCount);
	}
	
	public static void addmsg(CrowdConsulting msg){
		User user = User.findById(SessionHelper.getCurrentUser(session).id);
		msg.from = user;
		msg.createDate = new Date();
		msg.save();
		consulting();
	}
	
	public static void editMsg(CrowdConsulting msg){
		CrowdConsulting crowdConsulting = CrowdConsulting.findById(msg.id);
		crowdConsulting.text = msg.text;
		crowdConsulting.createDate = new Date();
		crowdConsulting.save();
		consulting();
	}
	
	public static void removeMessage(Long msgId){
		CrowdConsulting crowdConsulting = CrowdConsulting.findById(msgId);
		crowdConsulting.from = null;
		crowdConsulting.save();
		crowdConsulting.delete();
		consulting();
	}
	
	public static void more(Integer page, int type) {
		String entName = type == 1 ? "CrowdDeveloping" : "CrowdConsulting";
		Query tQuery = JPA.em().createQuery("select t from " + entName + " t order by t.createDate desc");
		tQuery.setMaxResults(10);
		tQuery.setFirstResult(page * 10);
		List<CrowdDeveloping> crowdMessages = tQuery.getResultList();
		User user = SessionHelper.getCurrentUser(session);
		Boolean isAdmin = user.role.equals(User.ROLE_ADMIN);
		String removeAction = type == 1 ? "removemessagef" : "removemessage";
		String editAction = type == 1 ? "editmsgf" : "editmsg";
		render(crowdMessages, isAdmin, user, removeAction, editAction);
	}
	
	public static void developing() {
		Query tQuery = JPA.em().createQuery("select t from CrowdDeveloping t order by t.createDate desc");
		tQuery.setMaxResults(10);
		List<CrowdDeveloping> crowdDeveloping = tQuery.getResultList();
		Query tQueryCount = JPA.em().createQuery("select count(cf.id) from CrowdDeveloping cf");
		Integer messagesCount = ((Long) tQueryCount.getResultList().get(0)).intValue();
		Boolean isAdmin = SessionHelper.getCurrentUser(session).role.equals(User.ROLE_ADMIN);
		render(crowdDeveloping, messagesCount, isAdmin);
	}
	
	public static void addmsgF(CrowdDeveloping msg){
		User user = User.findById(SessionHelper.getCurrentUser(session).id);
		msg.from = user;
		msg.createDate = new Date();
		msg.save();
		developing();
	}
	
	public static void editMsgF(CrowdDeveloping msg){
		CrowdDeveloping crowdDeveloping = CrowdDeveloping.findById(msg.id);
		crowdDeveloping.text = msg.text;
		crowdDeveloping.createDate = new Date();
		crowdDeveloping.save();
		developing();
	}
	
	public static void removeMessageF(Long msgId){
		CrowdDeveloping crowdDeveloping = CrowdDeveloping.findById(msgId);
		crowdDeveloping.from = null;
		crowdDeveloping.save();
		crowdDeveloping.delete();
		developing();
	}
	
}
