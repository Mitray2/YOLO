package notifiers;

import models.User;
import play.i18n.Messages;
import play.mvc.Mailer;
import utils.PasswordGenerator;
import utils.SecurityHelper;

public class Mails extends Mailer {

	// public static void newsEmail(User user, Post post) {
	// setSubject(post.title);
	// addRecipient(user.email);
	// setFrom("info@coachmen.ru");
	// // for attachment some files
	// // EmailAttachment attachment = new EmailAttachment();
	// // attachment.setDescription("A pdf document");
	// // attachment.setPath(Play.getFile("rules.pdf").getPath());
	// // addAttachment(attachment);
	// send(user, post);
	// }

	public static void firstTestPassed(User user, String base) {
		setSubject(Messages.get("mail.subject.type1"));
		addRecipient(user.email);
		setFrom("noreply@startnewteam.com");
		String password = PasswordGenerator.generate();
		String hash = user.mailTicket = user.passwordHash = SecurityHelper.createPasswordHash(password);
		user.save();
		send(user, password, hash, base);
	}

	public static void secondTestPassed(User user, String base) {
		setSubject(Messages.get("mail.subject.type2"));
		addRecipient(user.email);
		setFrom("noreply@startnewteam.com");
		String password = PasswordGenerator.generate();
		String hash = user.mailTicket = user.passwordHash = SecurityHelper.createPasswordHash(password);
		user.save();
		send(user, hash, base);
	}

	public static void blankFormPassed(User user, String base) {
		setSubject(Messages.get("mail.subject.type3"));
		addRecipient(user.email);
		setFrom("noreply@startnewteam.com");
		String hash = user.mailTicket = SecurityHelper.createPasswordHash(user.email);
		user.save();
		send(user, hash, base);
	}

	public static void groupRequest(User user, String base, String name, String text) {
		setSubject(Messages.get("mail.subject.type4"));
		addRecipient(user.email);
		setFrom("noreply@startnewteam.com");
		send(user, name, base, text);
	}

	public static void memberRequest(User user, String base, String name, String text) {
		setSubject(Messages.get("mail.subject.type5"));
		addRecipient(user.email);
		setFrom("noreply@startnewteam.com");
		send(user, base, name, text);
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
		setSubject(Messages.get("mail.subject.type6") + " " + base);
		addRecipient(user.email);
		setFrom("noreply@startnewteam.com");
		String newpassword = PasswordGenerator.generate();
		user.passwordHash = SecurityHelper.createPasswordHash(newpassword);
		user.save();
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

}