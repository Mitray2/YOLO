package controllers;

import java.util.List;

import models.Post;
import play.mvc.Controller;

public class NewsController extends BasicController {

	public static void index(){
		List<Post> newsList = Post.find("select p from Post p where p.type = ? and p.state = ? order by p.creationDate DESC", Post.TYPE_NEWS, Post.STATE_WORK).fetch();
		render(newsList);
	}
	
	public static void showNews(Long id){
		Post news = Post.findById(id);
		render(news);
	}
	
}
