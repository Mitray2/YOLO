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
import utils.PasswordGenerator;
import utils.SecurityHelper;

import java.util.Collection;

public class Mails extends Mailer {

    private static final String EMAIL_FROM = "StartNewTeam <noreply@startnewteam.com>";
    public static final String BASE_URL = Play.configuration.getProperty("application.baseUrl");

    private static void setRecipients(User user) {
        if(Play.mode.isDev()){
            addRecipient("siarzh@gmail.com");
            addRecipient("dzyakanau.d@gmail.com");
        }else{
            addRecipient(user.email);
        }
    }


	public static void firstTestPassed(User user, String base) {
        String userLang = user.preferredLang;
		setSubject(Messages.getMessage(userLang, "mail.subject.type1"));
		addRecipient(user.email);
		setFrom(EMAIL_FROM);
		String password = PasswordGenerator.generate();
		String hash = user.mailTicket = user.passwordHash = SecurityHelper.createPasswordHash(password);
		user.save();
        Lang.set(userLang);
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
        Lang.set(userLang);
		send(user, hash, base);
	}

	public static void blankFormPassed(User user, String base) {
        String userLang = user.preferredLang;
		setSubject(Messages.getMessage(userLang, "mail.subject.type3"));
		addRecipient(user.email);
		setFrom(EMAIL_FROM);
		String hash = user.mailTicket = SecurityHelper.createPasswordHash(user.email);
		user.save();
        Lang.set(userLang);
		send(user, hash, base);
	}

	public static void groupRequest(User user, String base, Command group, String text) {
        String userLang = user.preferredLang;
		setSubject(Messages.getMessage(userLang, "mail.subject.type4"));
		addRecipient(user.email);
		setFrom(EMAIL_FROM);
        Lang.set(userLang);
		send(user, group, base, text);
	}

	public static void memberRequest(User user, String base, User joinUser, String text) {
        String userLang = user.preferredLang;
		setSubject(Messages.getMessage(userLang, "mail.subject.type5"));
		addRecipient(user.email);
		setFrom(EMAIL_FROM);
        Lang.set(userLang);
		send(user, base, joinUser, text);
	}

	public static void teamMemberApproved(User user) {
        String userLang = user.preferredLang;
		setSubject(Messages.getMessage(userLang, "mail.subject.team.member.approved"));
        setRecipients(user);
        setFrom(EMAIL_FROM);

        if("ru".equals(userLang)){
            send("Mails/ru/teamMemberApproved", user);
        } else {
            send(user);
        }
	}

	public static void teamMemberDeclined(User user, Command team) {
        String userLang = user.preferredLang;
		setSubject(Messages.getMessage(userLang, "mail.subject.team.member.declined"));
        setRecipients(user);
        setFrom(EMAIL_FROM);

        if("ru".equals(userLang)){
            send("Mails/ru/teamMemberDeclined", user, team);
        } else {
            send(user, team);
        }
	}

	public static void teamInvitationAccepted(User teamAdmin, User potentialMember) {
        String userLang = teamAdmin.preferredLang;
		setSubject(Messages.getMessage(userLang, "mail.subject.team.invite.accepted"));
        setRecipients(teamAdmin);
        setFrom(EMAIL_FROM);

        if("ru".equals(userLang)){
            send("Mails/ru/teamInvitationAccepted", teamAdmin, potentialMember);
        } else {
            send(teamAdmin, potentialMember);
        }
	}

	public static void teamInvitationDeclined(User teamAdmin, User potentialMember) {
        String userLang = teamAdmin.preferredLang;
		setSubject(Messages.getMessage(userLang, "mail.subject.team.invite.declined"));
        setRecipients(teamAdmin);
        setFrom(EMAIL_FROM);

        if("ru".equals(userLang)){
            send("Mails/ru/teamInvitationDeclined", teamAdmin, potentialMember);
        } else {
            send(teamAdmin, potentialMember);
        }
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

    public static void unreadMessage(Message newMessage) {
        User user = newMessage.to;
        String userLang = user.preferredLang;

        setSubject(String.format("%s %s %s", Messages.getMessage(userLang, "mail.subject.unread.message"),
                user.name, user.lastName));
        setRecipients(user);
        setFrom(EMAIL_FROM);

        if("ru".equals(userLang)){
            send("Mails/ru/unreadMessage", user, newMessage);
        } else {
            send(user, newMessage);
        }
    }

    public static void recentUserActivity(User user, UserActivity activity) {
        String userLang = user.preferredLang;

        setSubject(Messages.getMessage(userLang, "mail.subject.daily.recap"));
        setRecipients(user);
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
        setRecipients(user);
        setFrom(EMAIL_FROM);
        Lang.set(userLang);
        send(user);
    }

    public static void newTeamGenerated(User user) {
        Logger.debug("MAIL: new team [%s] generated for user [%d](%d)", user.command == null ? "NULL" : user.command.name, user.id, user.role);

        String userLang = user.preferredLang;

        setSubject(Messages.getMessage(userLang, "mail.subject.teams.auto.".concat(
                user.role.equals(User.ROLE_GROUP_ADMIN) ? "admin" : "user"
        )));
        setRecipients(user);
        setFrom(EMAIL_FROM);

        if("ru".equals(userLang)){
            send("Mails/ru/newTeamGenerated", user);
        } else {
            send(user);
        }
    }

    public static void addMoreMembersToTeam(User user) {
        String userLang = user.preferredLang;
        setSubject(Messages.getMessage(userLang, "mail.subject.teams.auto.add_more"));
        setRecipients(user);
        setFrom(EMAIL_FROM);

        if("ru".equals(userLang)){
            send("Mails/ru/addMoreMembersToTeam", user);
        } else {
            send(user);
        }
    }

    public static void platformNews(User user, Collection<Post> news) {
        setSubject(Messages.getMessage(user.preferredLang, "mail.subject.platform.news"));
        setRecipients(user);
        setFrom(EMAIL_FROM);
        send(user,news);
    }

}