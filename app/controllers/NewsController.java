package controllers;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import models.Post;
import play.i18n.Lang;
import play.mvc.Controller;
import play.mvc.Http.Cookie;

public class NewsController extends BasicController {

	private static final String NEWS_LANG = "newsLang";
	
	public static void index(String lang){
		//by default news lang the same as user locale
		String newsLang = Lang.get();

		//check if another news lang saved in cookies
		Cookie newsLangCookie = request.cookies.get(NEWS_LANG);
		if (newsLangCookie != null && StringUtils.isNotEmpty(newsLangCookie.value)) {
			System.out.println("news lang in cookie = " + newsLangCookie.value);
			newsLang = newsLangCookie.value;
		}
		
		if (StringUtils.isNotEmpty(lang)) {
			//save selected news lang to cookies
			response.setCookie(NEWS_LANG, lang);
			newsLang = lang;
		}
		
		
		Integer langId = newsLang.equals("ru") ? 0 : 1;
		System.out.println("news lang = " + newsLang);
		List<Post> newsList = Post.find("select p from Post p where p.type = ? and p.state = ? and lang = ? order by p.creationDate DESC", Post.TYPE_NEWS, Post.STATE_WORK, langId).fetch();
		render(newsList, newsLang);
	}
	
	public static void showNews(Long id){
		Post news = Post.findById(id);
		render(news);
	}
	
}
