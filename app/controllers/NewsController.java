package controllers;

import models.Post;
import play.Logger;
import play.mvc.Catch;
import utils.LangUtils;

import java.util.List;

public class NewsController extends BasicController {

	private static final String NEWS_LANG = "newsLang";


    @Catch(value = Throwable.class, priority = 1)
    public static void onError(Throwable e) {
        Logger.error(e, "[NewsCTR] %s", e.getMessage());
        ApplicationController.index();
    }
	
	public static void index(String lang){
		String newsLang = LangUtils.getNewsLang(lang, request, response, NEWS_LANG);
		Integer langId = LangUtils.langToId(newsLang);
		List<Post> newsList = Post.find("select p from Post p where p.type = ? and p.state = ? and lang = ? order by p.creationDate DESC", Post.TYPE_NEWS, Post.STATE_WORK, langId).fetch();
		render(newsList, newsLang);
	}
	
	public static void showNews(Long id){
        Post news = null;
        try{
		    news = Post.findById(id);
        } catch (Exception e) {
            NewsController.index(null);
        }

        if(news != null){
		    render(news);
        } else {
            index(null);
        }
	}
	
}
