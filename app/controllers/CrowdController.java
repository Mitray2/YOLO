package controllers;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import models.CrowdConsulting;
import models.CrowdFunding;
import models.Topic;
import models.TopicMessage;
import models.User;
import play.db.jpa.JPA;
import play.mvc.Before;
import play.mvc.Controller;
import utils.ApplicationConstants;
import utils.SessionHelper;

public class CrowdController extends Controller implements ApplicationConstants {

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
		String entName = type == 1 ? "CrowdFunding" : "CrowdConsulting";
		Query tQuery = JPA.em().createQuery("select t from " + entName + " t order by t.createDate desc");
		tQuery.setMaxResults(10);
		tQuery.setFirstResult(page * 10);
		List<CrowdFunding> crowdMessages = tQuery.getResultList();
		User user = SessionHelper.getCurrentUser(session);
		Boolean isAdmin = user.role.equals(User.ROLE_ADMIN);
		String removeAction = type == 1 ? "removemessagef" : "removemessage";
		String editAction = type == 1 ? "editmsgf" : "editmsg";
		render(crowdMessages, isAdmin, user, removeAction, editAction);
	}
	
	public static void funding() {
		Query tQuery = JPA.em().createQuery("select t from CrowdFunding t order by t.createDate desc");
		tQuery.setMaxResults(10);
		List<CrowdFunding> crowdFunding = tQuery.getResultList();
		Query tQueryCount = JPA.em().createQuery("select count(cf.id) from CrowdFunding cf");
		Integer messagesCount = ((Long) tQueryCount.getResultList().get(0)).intValue();
		Boolean isAdmin = SessionHelper.getCurrentUser(session).role.equals(User.ROLE_ADMIN);
		render(crowdFunding, messagesCount, isAdmin);
	}
	
	public static void addmsgF(CrowdFunding msg){
		User user = User.findById(SessionHelper.getCurrentUser(session).id);
		msg.from = user;
		msg.createDate = new Date();
		msg.save();
		funding();
	}
	
	public static void editMsgF(CrowdFunding msg){
		CrowdFunding crowdFunding = CrowdFunding.findById(msg.id);
		crowdFunding.text = msg.text;
		crowdFunding.createDate = new Date();
		crowdFunding.save();
		funding();
	}
	
	public static void removeMessageF(Long msgId){
		CrowdFunding crowdFunding = CrowdFunding.findById(msgId);
		crowdFunding.from = null;
		crowdFunding.save();
		crowdFunding.delete();
		funding();
	}
	
}
