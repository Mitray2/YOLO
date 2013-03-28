package notifiers;

import modelDTO.UserActivity;
import models.Command;
import models.Message;
import models.Post;
import models.User;
import play.Logger;
import play.Play;
import play.i18n.Lang;
import play.i18n.Messages;
import play.mvc.Mailer;
import utils.LangUtils;
import utils.PasswordGenerator;
import utils.SecurityHelper;

import java.util.List;
import java.util.Locale;

public class Mails extends Mailer {

  private static final String EMAIL_FROM = "StartNewTeam <noreply@startnewteam.com>";
  public static final String BASE_URL = Play.configuration.getProperty("application.baseUrl");


	public static void firstTestPassed(User user, String base) {
        String userLang = user.preferredLang;
		setSubject(Messages.getMessage(userLang, "mail.subject.type1"));
		addRecipient(user.email);
		setFrom(EMAIL_FROM);
		String password = PasswordGenerator.generate();
		String hash = user.mailTicket = user.passwordHash = SecurityHelper.createPasswordHash(password);
		user.save();
		send(user, password, hash, base);
	}

	public static void secondTestPassed(User user, String base) {
        String userLang = user.preferredLang;
		setSubject(Messages.getMessage(userLang, "mail.subject.type2"));
		addRecipient(user.email);
		setFrom(EMAIL_FROM);
		String password = PasswordGenerator.generate();
		String hash = user.mailTicket = user.passwordHash = SecurityHelper.createPasswordHash(password);
		user.save();
		send(user, hash, base);
	}

	public static void blankFormPassed(User user, String base) {
        String userLang = user.preferredLang;
		setSubject(Messages.getMessage(userLang, "mail.subject.type3"));
		addRecipient(user.email);
		setFrom(EMAIL_FROM);
		String hash = user.mailTicket = SecurityHelper.createPasswordHash(user.email);
		user.save();
		send(user, hash, base);
	}

	public static void groupRequest(User user, String base, Command group, String text) {
        String userLang = user.preferredLang;
		setSubject(Messages.getMessage(userLang, "mail.subject.type4"));
		addRecipient(user.email);
		setFrom(EMAIL_FROM);
		send(user, group, base, text);
	}

	public static void memberRequest(User user, String base, User joinUser, String text) {
        String userLang = user.preferredLang;
		setSubject(Messages.getMessage(userLang, "mail.subject.type5"));
		addRecipient(user.email);
		setFrom(EMAIL_FROM);
		send(user, base, joinUser, text);
	}

	// public static void feedback(String email, String message) {
	// setSubject("Фидбек от пользователя - " + email);
	// addRecipient("al1ks.moiseev@gmail.com");
	// addRecipient("ivgorodko@gmail.com");
	// addRecipient("lexeybsuir@gmail.com");
	// setFrom("info@coachmen.ru");
	// send(email, message);
	// }

	public static void lostPassword(User user, String base) {
        String userLang = user.preferredLang;
		setSubject(Messages.getMessage(userLang, "mail.subject.type6") + " " + base);
		addRecipient(user.email);
		setFrom(EMAIL_FROM);
		String newpassword = PasswordGenerator.generate();
		user.passwordHash = SecurityHelper.createPasswordHash(newpassword);
		user.save();
        Lang.set(userLang);
		send(user, newpassword);
	}

	// public static void error(String errorMessage){
	// setSubject("Ошибка");
	// // addRecipient("al1ks.moiseev@gmail.com");
	// // addRecipient("ivgorodko@gmail.com");
	// addRecipient("lexeybsuir@gmail.com");
	// addRecipient("zhenya.yadlovskij@gmail.com");
	// setFrom("info@coachmen.ru");
	// send(errorMessage);
	// }

    public static void recentUserActivity(User user, UserActivity activity) {
        String userLang = user.preferredLang;
        Logger.debug("user preferred lang: " + userLang);
        setSubject(Messages.getMessage(userLang, "mail.subject.unread.messages"));
        //addRecipient(user.email); TODO uncomment for prod
        addRecipient("siarzh@gmail.com");
        addRecipient("dzyakanau.d@gmail.com");
        setFrom(EMAIL_FROM);

        if("ru".equals(userLang)){
            send("Mails/ru/recentUserActivity", user, activity);
        } else {
            send(user, activity);
        }
    }

    public static void notSeenForAWeek(User user) {
        String userLang = user.preferredLang;
        setSubject(Messages.getMessage(userLang, "mail.subject.not.seen"));
        //addRecipient(user.email); TODO uncomment for prod
        addRecipient("siarzh@gmail.com");
        addRecipient("dzyakanau.d@gmail.com");
        setFrom(EMAIL_FROM);
        Lang.set(userLang);
        send(user);
    }

    public static void platformNews(User user, Post post) {
        if( LangUtils.langToId(user.preferredLang).equals(post.lang) ){
            setSubject(post.title);
            //addRecipient(user.email); TODO uncomment for prod
            addRecipient("siarzh@gmail.com");
            addRecipient("dzyakanau.d@gmail.com");
            setFrom(EMAIL_FROM);
            send(post);
        }
    }

}