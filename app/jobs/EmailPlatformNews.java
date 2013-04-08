package jobs;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimaps;
import models.NotificationType;
import models.Post;
import models.User;
import notifiers.Mails;
import play.Logger;
import play.db.jpa.JPA;
import play.jobs.Job;
import play.jobs.On;
import utils.DateUtils;
import utils.LangUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.is;

/** Sends latest platform new to users via email **/
@On("0 0 17 ? * WED") // fires every Wednesday at 17:00 MSK
public class EmailPlatformNews extends Job {

    private static Function<Post, String> pickToUserFn = new Function<Post, String>() {
        @Override
        public String apply(Post news) {
            return LangUtils.idToLang(news.lang);
        }
    };


    public void doJob() {
        Logger.debug("------------ JOB:NEWS: platform news ------------");

        // 1. select latest week unsent news
        List<Post> recentNews = Post.find("state = ? AND sent = 0 AND type = ? AND creationDate > ? ORDER BY creationDate DESC",
                Post.STATE_WORK, Post.TYPE_NEWS, DateUtils.weekAgo()).fetch();

        if(recentNews.size() > 0){

            // 2. if there are some, select all users who wants to be notified on platform news
            List<User> users = User.find("select u from User u join u.notifications n where u.role <> ? AND n.id = ?",
                    User.ROLE_ADMIN, NotificationType.PLATFORM_NEWS).fetch();

            if(users.size() > 0){

                // 3. map news to its languages (en, ru only at the moment)
                ImmutableListMultimap<String, Post> langNewsMap = Multimaps.index(recentNews, pickToUserFn);
                for (Map.Entry<String, Collection<Post>> langNews : langNewsMap.asMap().entrySet()) {
                    String lang = langNews.getKey();
                    Collection<Post> newsForLang = langNews.getValue();

                    // 4. sent each user all recent news in his/her language
                    List<User> langUsers = filter(having(on(User.class).preferredLang, is(lang)), users);
                    List<Long> langUserIds = extract(langUsers, on(User.class).id);
                    List<Long> langNewsIds = extract(newsForLang, on(Post.class).id);
                    Logger.debug("JOB:NEWS: %d users to notify with news [%s] in lang [%s]: %s",
                            langUserIds.size(), Arrays.toString(langNewsIds.toArray()),  lang, Arrays.toString(langUserIds.toArray()));
                    for (User langUser : langUsers) {
                        Mails.platformNews(langUser, newsForLang);
                    }
                }
            } else {
                Logger.debug("JOB:NEWS: no users to notify of platform news :(");
            }

            // 5. mark all recent news as sent
            List<Long> newsIds = extract(recentNews, on(Post.class).id);
            int result = JPA.em().createQuery("update Post set sent = 1 where id IN (:ids)")
                                 .setParameter("ids", newsIds).executeUpdate();

            Logger.debug("JOB:NEWS: %d news sent: %s", result, Arrays.toString(newsIds.toArray()));

        } else {
            Logger.debug("JOB:NEWS: no platform news this week :(");
        }

        Logger.debug("---------------------------------------------");
    }

}
