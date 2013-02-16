package controllers;

import models.*;
import models.predicate.FindAnswerByIdPredicate;
import models.predicate.FindAnswerByQuestionIdPredicate;
import models.validators.UserUniqueEmailValidator;
import notifiers.Mails;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import play.data.validation.Required;
import play.i18n.Messages;
import utils.ApplicationConstants;
import utils.ModelUtils;
import utils.SecurityHelper;
import utils.SessionData.SessionUserMessage;
import utils.SessionHelper;

import java.util.*;

public class LoginController extends BasicController implements ApplicationConstants {

	public static void firstTest(User user) {
		Integer birthYear = Integer.parseInt(params.get("birthYear"));
		Integer birthMonth = Integer.parseInt(params.get("birthMonth"));
		Integer birthDay = Integer.parseInt(params.get("birthDay"));

		user.email = user.email.toLowerCase();

		validation.required("user.email", user.email).message(VALIDATION_MODEL_USER_EMAIL_REQUIRED);
		validation.required("user.sex", user.sex).message("validation.model.user.sex.required");

		validation.required("user.name", user.name).message(VALIDATION_MODEL_USER_NAME_REQUIRED);
		if (!validation.hasError("user.name"))
			validation.maxSize("user.name", user.name, 30).message(VALIDATION_MODEL_USER_NAME_MAX_LENGTH);
//		if (!validation.hasError("user.name"))
//			validation.match("user.name", user.name, "[А-Яа-я\\-\\s]+").message(VALIDATION_MODEL_USER_NAME_INVALID);
		if (!validation.hasError("user.email"))
			validation.email("user.email", user.email).message("validation.model.user.email.invalid");
		if (!validation.hasError("user.email"))
			validation.isTrue("user.email", new UserUniqueEmailValidator().isSatisfied(user, user.email)).message("validation.model.user.email.dublicated");

		if (validation.hasErrors()) {
            Long usersCount = User.count();
            Long commandsCount = Command.count();
			render("ApplicationController/index.html", user, validation.errors(), birthDay, birthMonth, birthYear, usersCount, commandsCount);
		}

		Test test = Test.findById(1l);
		render(test, user, birthYear, birthMonth, birthDay);
	}

	public static void firstTestPassed(User user, Test test, String birthYear, String birthMonth, String birthDay) {
		// in case user will want to reaload result page
		User alereadySavedUser = User.find("email = ?", user.email).first();
		if (alereadySavedUser == null) {
			test.refresh();
			FindAnswerByIdPredicate findAnswerByIdPredicate = new FindAnswerByIdPredicate();
			int result = 0;

			for (int i = 1; i <= test.questions.size(); i++) {
				Question question = test.questions.get(i - 1);
				if (question != null) {
					if (question.answer != null) {
						findAnswerByIdPredicate.setId(question.answer.id);
						Answer findedAnwer = (Answer) CollectionUtils.find(question.answers, findAnswerByIdPredicate);
						if (findedAnwer != null) {
							result += findedAnwer.score;
						}
					} else {
						validation.required(VALIDATION_MODEL_TEST_QUESTION_ANSWER_REQUIRED + question.id, question.answer).message(VALIDATION_MODEL_TEST_QUESTION_ANSWER_REQUIRED);
					}
				}
			}

			if (validation.hasErrors()) {
				Test firstTest = Test.findById(test.id);
				FindAnswerByQuestionIdPredicate findAnswerByQuestionIdPredicate = new FindAnswerByQuestionIdPredicate();
				for (Question question : firstTest.questions) {
					if (test != null) {
						for (Question testQuestion : test.questions) {
							if (testQuestion != null && question.id.equals(testQuestion.id)) {
								question.answer = testQuestion.answer;
							}
						}
					}
				}
				render("LoginController/firstTest.html", test, user, validation.errors(), birthDay, birthMonth, birthYear);
			}

			user.name = StringUtils.capitalize(user.name);
			user.email = user.email.toLowerCase();
			user.role = User.ROLE_INPERFECT_USER;
			Calendar birthday = Calendar.getInstance();
			birthday.set(Integer.parseInt(birthYear), Integer.parseInt(birthMonth), Integer.parseInt(birthDay));
			user.birthday = birthday.getTime();
			ModelUtils.calculateUsersAge(user);
			user.regDate = new Date();
			user.businessman = result;

			Mails.firstTestPassed(user, request.getBase());

			alereadySavedUser = user;
		}
		render(alereadySavedUser);
	}

	public static void secondTest() {
		User user = SessionHelper.getCurrentUser(session);
		if (user != null) {
			Test test = Test.findById(2l);
			render(test);
		} else {
			CommonController.error(CommonController.ERROR_UNAUTHORIZED_USER);
		}
	}

	public static void secondTestPassed() {
		User currentUser = User.findById(SessionHelper.getCurrentUser(session).id);
		if (currentUser.role.equals(User.ROLE_WITHOUT_BLANK)) {
			render(currentUser);
		} else {
			List<ParsingResult> parsingResults = new ArrayList<LoginController.ParsingResult>();
			// parsing request
			for (Map.Entry<String, String[]> entry : request.params.all().entrySet()) {
				String parameter = entry.getKey();
				if (parameter.startsWith("test.questions")) {
					int firstOpenBracket = parameter.indexOf("[");
					int firstCloseBracket = parameter.indexOf("]");
					int secondOpenBracket = parameter.indexOf("[", firstOpenBracket + 1);
					int secondCloseBracket = parameter.indexOf("]", firstCloseBracket + 1);
					final int questionNumber = Integer.parseInt(parameter.substring(firstOpenBracket + 1, firstCloseBracket));
					final int innerQuestionNumber = Integer.parseInt(parameter.substring(secondOpenBracket + 1, secondCloseBracket));
					final long answerId = Long.parseLong(entry.getValue()[0]);
					parsingResults.add(new ParsingResult() {
						@Override
						public Integer getQuestionNumber() {
							return questionNumber;
						}

						@Override
						public Integer getInnerQuestionNumber() {
							return innerQuestionNumber;
						}

						@Override
						public Long getAnswerId() {
							return answerId;
						}
					});
				}
			}

			// filling the test
			Test test = Test.findById(2l);
			FindAnswerByIdPredicate findAnswerByIdPredicate = new FindAnswerByIdPredicate();
			for (ParsingResult parsingResult : parsingResults) {
				Question innerQuestion = test.questions.get(parsingResult.getQuestionNumber() - 1).questions.get(parsingResult.getInnerQuestionNumber() - 1);
				findAnswerByIdPredicate.setId(parsingResult.getAnswerId());
				Answer answer = (Answer) CollectionUtils.find(innerQuestion.answers, findAnswerByIdPredicate);
				innerQuestion.answer = answer;
			}

			// validation and filling the user
			float pragmatist = 0f;
			float communicant = 0f;
			float idealist = 0f;
			for (int i = 0; i < test.questions.size(); i++) {
				Question question = test.questions.get(i);
				for (Question innerQuestion : question.questions) {
					Answer answer = innerQuestion.answer;
					validation.required(ApplicationConstants.VALIDATION_MODEL_TEST_QUESTION_INNER_QUESTION_ANSWER_REQUIRED + innerQuestion.id, answer).message(
							ApplicationConstants.VALIDATION_MODEL_TEST_QUESTION_INNER_QUESTION_ANSWER_REQUIRED);
					if (validation.hasError(ApplicationConstants.VALIDATION_MODEL_TEST_QUESTION_INNER_QUESTION_ANSWER_REQUIRED + innerQuestion.id))
						continue;
					if (answer.userParameter.equals(Answer.USER_PARAMETER_PRAGMATIST)) {
						pragmatist += answer.score;
					} else if (answer.userParameter.equals(Answer.USER_PARAMETER_COMMUNICANT)) {
						communicant += answer.score;
					} else {
						idealist += answer.score;
					}
				}
			}

			if (validation.hasErrors()) {
				render("LoginController/secondTest.html", validation.errors(), test);
			}

			currentUser.pragmatist = Math.round(pragmatist);
			currentUser.communicant = Math.round(communicant);
			currentUser.idealist = Math.round(idealist);

			if (currentUser.pragmatist + currentUser.communicant + currentUser.idealist > 200) {
				if (currentUser.pragmatist > currentUser.communicant && currentUser.pragmatist > currentUser.idealist)
					currentUser.pragmatist--;
				if (currentUser.communicant > currentUser.pragmatist && currentUser.communicant > currentUser.idealist)
					currentUser.communicant--;
				if (currentUser.idealist > currentUser.communicant && currentUser.idealist > currentUser.pragmatist)
					currentUser.idealist--;
			}

			// for not to save test.
			test.em().detach(test);
			SessionHelper.setCurrentUser(session, currentUser);
			currentUser.role = User.ROLE_WITHOUT_BLANK;
			currentUser.save();
			SessionHelper.setCurrentUser(session, currentUser);
			render(currentUser);
		}
	}

	public static void blankForm() {
		User user = SessionHelper.getCurrentUser(session);
		render(user);
	}

	public static void blankFormPassed(User user) {
		String password = params.get("password");
		String passwordRepeating = params.get("passwordRepeating");

		validation.required(password).message(VALIDATION_MODEL_USER_PASSORD_REQUIRED);
		if (!validation.hasError("password"))
			validation.minSize(password, 8).message(VALIDATION_MODEL_USER_PASSORD_MIN_LENGTH);
		if (!validation.hasError("password"))
			validation.maxSize(password, 16).message(VALIDATION_MODEL_USER_PASSORD_MAX_LENGTH);
		if (!validation.hasError("password"))
			validation.match(password, "[A-Za-z0-9\\.\\-\\_]+").message(VALIDATION_MODEL_USER_PASSORD_INVALID);
		validation.required(passwordRepeating).message(VALIDATION_MODEL_USER_PASSORD_REPEATING_REQUIRED);
		if (!validation.hasError("password") && !validation.hasError("passwordRepeating"))
			validation.equals(password, passwordRepeating).message(VALIDATION_MODEL_USER_PASSORD_REPEATING_INVALID);
		validation.required("user.lastName", user.lastName).message(VALIDATION_MODEL_USER_LAST_NAME_REQUIRED);
		if (!validation.hasError("user.lastName"))
			validation.maxSize("user.lastName", user.lastName, 30).message(VALIDATION_MODEL_USER_LAST_NAME_MAX_LENGTH);
//		if (!validation.hasError("user.lastName"))
//			validation.match("user.lastName", user.lastName, "[А-Яа-я\\-\\s]+").message(VALIDATION_MODEL_USER_LAST_NAME_INVALID);
		validation.required("user.city", user.city).message(VALIDATION_MODEL_USER_CITY_REQUIRED);
		if (!validation.hasError("user.city"))
			validation.maxSize("user.city", user.city, 30).message(VALIDATION_MODEL_USER_CITY_MAX_LENGTH);
//		if (!validation.hasError("user.city"))
//			validation.match("user.city", user.city, "[А-Яа-я\\-\\s]+").message(VALIDATION_MODEL_USER_CITY_INVALID);

		// validation.required("user.personalCV",
		// user.personalCV).message(VALIDATION_MODEL_USER_PERSONAL_CV_REQUIRED);
		// validation.maxSize("user.personalCV", user.personalCV,
		// 300).message(VALIDATION_MODEL_USER_PERSONAL_CV_MAX_LENGTH);
		// validation.required("user.bType",
		// user.businessType).message(VALIDATION_MODEL_USER_BUSINESS_TYPE);
		// validation.required("user.bSphere",
		// user.businessSphere).message(VALIDATION_MODEL_USER_BUSINESS_SPHERE);
		// validation.maxSize("user.expMarketing",
		// user.expMarketing.description,
		// 300).message(VALIDATION_MODEL_USER_EXP_MARKETING_MAX_LENGTH);
		// validation.maxSize("user.expSale", user.expSale.description,
		// 300).message(VALIDATION_MODEL_USER_EXP_SALE_MAX_LENGTH);
		// validation.maxSize("user.expManagement",
		// user.expManagement.description,
		// 300).message(VALIDATION_MODEL_USER_EXP_MANAGEMENT_MAX_LENGTH);
		// validation.maxSize("user.expFinance", user.expFinance.description,
		// 300).message(VALIDATION_MODEL_USER_EXP_FINANCE_MAX_LENGTH);
		// validation.maxSize("user.expLegal", user.expLegal.description,
		// 300).message(VALIDATION_MODEL_USER_EXP_LEGAL_MAX_LENGTH);
		// validation.maxSize("user.expIT", user.expIT.description,
		// 300).message(VALIDATION_MODEL_USER_EXP_IT_MAX_LENGTH);
		// validation.maxSize("user.expOther", user.expOther.description,
		// 300).message(VALIDATION_MODEL_USER_EXP_OTHER_MAX_LENGTH);

		if (validation.hasErrors()) {
			render("LoginController/blankForm.html", validation.errors(), user);
		}

		User userToSave = User.findById(user.id);
		userToSave.passwordHash = SecurityHelper.createPasswordHash(password);
		ModelUtils.fillUser(userToSave, user);
		Mails.blankFormPassed(userToSave, request.getBase());
		SessionHelper.logout(session);
		render(userToSave);
	}

	public static void confirmRegistration(@Required String ticket) {
		if (StringUtils.isNotEmpty(ticket)) {
			User user = User.find(" mailTicket = ? ", ticket).first();
			if (user != null) {
				if (user.role.equals(User.ROLE_INPERFECT_USER)) {
					SessionHelper.setCurrentUser(session, user);
					redirect(request.getBase() + "/secondTest/");
				} else if (user.role.equals(User.ROLE_WITHOUT_BLANK) && StringUtils.isEmpty(user.lastName)) {
					SessionHelper.setCurrentUser(session, user);
					redirect(request.getBase() + "/blankForm/");
				} else {
					user.mailTicket = null;
					user.role = User.ROLE_USER;
					user.save();
					SessionHelper.setCurrentUser(session, user);
					redirect(request.getBase() + "/");
				}
			} else {
				validation.isTrue(false).message(VALIDATION_LOGIN_CONTROLLER_VALIDATION_HAS_BEEN_APPROVED);
				render("ApplicationController/index.html", validation.errors());
			}
		} else {
			validation.isTrue(false).message(VALIDATION_LOGIN_CONTROLLER_VALIDATION_HAS_BEEN_APPROVED);
			render("ApplicationController/index.html", validation.errors());
		}
	}

	public static void login(String loginEmail, String loginPassword) {
		validation.required("loginEmail", loginEmail).message(Messages.get("common.login.error.email_empty"));
		validation.required("loginPassword", loginPassword).message(Messages.get("common.login.error.password_empty"));
		if (!validation.hasErrors()) {
			User user = User.find("email = ? and passwordHash = ?", loginEmail, SecurityHelper.createPasswordHash(loginPassword)).first();
			if (user != null) {
				if (user.role != User.ROLE_INPERFECT_USER && user.mailTicket != null) {
					validation.addError("emailPassword", VALIDATION_LOGIN_CONTROLLER_APPROVE_REGISTRATION, new String[] {});
					render("ApplicationController/index.html", validation.errors());
				}
				SessionHelper.setCurrentUser(session, user);
				redirect(request.getBase() + SLASH);
			} else {
				validation.addError("emailPassword", VALIDATION_LOGIN_CONTROLLER_INCORRECT_LOGIN_OR_PASSWORD, new String[] {});
				render("ApplicationController/index.html", validation.errors());
			}
		} else {
			render("ApplicationController/index.html", validation.errors());
		}
	}

	public static void logout() {
		SessionHelper.logout(session);
		ApplicationController.index();
	}

	public static void index() {
		render();
	}

	public static void recoverPassword(String recoverEmail) {
		User user = null;
		validation.required("recoverEmail", recoverEmail).message(Messages.get("common.login.error.email_empty"));
		if (!validation.hasError("recoverEmail"))
			validation.email("recoverEmail", recoverEmail).message(Messages.get("common.login.error.email_format_invalid"));
		if (!validation.hasError("recoverEmail")) {
			user = User.find(" email = ?", recoverEmail).first();
			validation.isTrue("recoverEmail", user != null).message(VALIDATION_LOGIN_CONTROLLER_NONEXISTENT_USER);
		}
		if (!validation.hasErrors()) {
			if (user.role == User.ROLE_INPERFECT_USER) {
				// user passed the first test
				Mails.firstTestPassed(user, request.getBase());
			} else if (user.role == User.ROLE_WITHOUT_BLANK) {
				// in case user fill out blank form and then click on recovering
				// password
				if (StringUtils.isNotEmpty(user.lastName)) {
					Mails.blankFormPassed(user, request.getBase());
				} else {
					// in case user clicked recovering having passed the second
					// test
					Mails.secondTestPassed(user, request.getBase());
				}
			} else {
				// registered user
				Mails.lostPassword(user, request.getBase());
			}
			SessionHelper.setUserMessage(session, new SessionUserMessage(Messages.get("common.login.password_sended")));
			ApplicationController.index();
		} else {
			render("ApplicationController/index.html", validation.errors());
		}
	}

    public static void register() {
        flash("registration", true);
        redirect(request.getBase());
    }

	private interface ParsingResult {
		Integer getQuestionNumber();

		Integer getInnerQuestionNumber();

		Long getAnswerId();
	}

}