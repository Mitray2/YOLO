package controllers;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import models.Post;
import play.i18n.Lang;
import play.mvc.Controller;
import play.mvc.Http.Cookie;
import utils.LangUtils;

public class NewsController extends BasicController {

	private static final String NEWS_LANG = "newsLang";
	
	public static void index(String lang){
		String newsLang = LangUtils.getNewsLang(lang, request, response, NEWS_LANG);
		Integer langId = LangUtils.langToId(newsLang);
		List<Post> newsList = Post.find("select p from Post p where p.type = ? and p.state = ? and lang = ? order by p.creationDate DESC", Post.TYPE_NEWS, Post.STATE_WORK, langId).fetch();
		render(newsList, newsLang);
	}
	
	public static void showNews(Long id){
		Post news = Post.findById(id);
		render(news);
	}
	
}
