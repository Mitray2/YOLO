package utils;

public interface ApplicationConstants {
	// TODO validation variables
	public static final String VALIDATION_LOGIN_CONTROLLER_VALIDATION_HAS_BEEN_APPROVED = "validation.loginController.registrationHasBeenApproved";

	public static final String VALIDATION_MODEL_USER_EMAIL_REQUIRED = "validation.model.user.email.required";
	public static final String VALIDATION_MODEL_USER_PASSORD_REQUIRED = "validation.model.user.password.required";
	public static final String VALIDATION_MODEL_USER_PASSORD_INVALID = "validation.model.user.password.invalid";
	public static final String VALIDATION_MODEL_USER_PASSORD_MIN_LENGTH = "validation.model.user.password.minLength";
	public static final String VALIDATION_MODEL_USER_PASSORD_MAX_LENGTH = "validation.model.user.password.maxLength";
	public static final String VALIDATION_MODEL_USER_PASSORD_REPEATING_REQUIRED = "validation.model.user.passwordRepeating.required";
	public static final String VALIDATION_MODEL_USER_PASSORD_REPEATING_INVALID = "validation.model.user.passwordRepeating.invalid";
	public static final String VALIDATION_MODEL_USER_NAME_REQUIRED = "validation.model.user.name.required";
	public static final String VALIDATION_MODEL_USER_NAME_MAX_LENGTH = "validation.model.user.name.maxLength";
	public static final String VALIDATION_MODEL_USER_NAME_INVALID = "validation.model.user.name.invalid";
	public static final String VALIDATION_MODEL_USER_LAST_NAME_REQUIRED = "validation.model.user.lastName.required";
	public static final String VALIDATION_MODEL_USER_LAST_NAME_MAX_LENGTH = "validation.model.user.lastName.maxLength";
	public static final String VALIDATION_MODEL_USER_LAST_NAME_INVALID = "validation.model.user.lastName.invalid";
	public static final String VALIDATION_MODEL_USER_CITY_REQUIRED = "validation.model.user.city.required";
	public static final String VALIDATION_MODEL_USER_CITY_MAX_LENGTH = "validation.model.user.city.maxLength";
	public static final String VALIDATION_MODEL_USER_CITY_INVALID = "validation.model.user.city.invalid";
	public static final String VALIDATION_MODEL_USER_EXP_MARKETING_MAX_LENGTH = "validation.model.user.expMarketing.maxLength";
	public static final String VALIDATION_MODEL_USER_EXP_SALE_MAX_LENGTH = "validation.model.user.expSale.maxLength";
	public static final String VALIDATION_MODEL_USER_EXP_MANAGEMENT_MAX_LENGTH = "validation.model.user.expManagement.maxLength";
	public static final String VALIDATION_MODEL_USER_EXP_FINANCE_MAX_LENGTH = "validation.model.user.expFinance.maxLength";
	public static final String VALIDATION_MODEL_USER_EXP_LEGAL_MAX_LENGTH = "validation.model.user.expLegal.maxLength";
	public static final String VALIDATION_MODEL_USER_EXP_IT_MAX_LENGTH = "validation.model.user.expIT.maxLength";
	public static final String VALIDATION_MODEL_USER_EXP_OTHER_MAX_LENGTH = "validation.model.user.expOther.maxLength";
	public static final String VALIDATION_MODEL_USER_PERSONAL_CV_MAX_LENGTH = "validation.model.user.personalCV.maxLength";
	public static final String VALIDATION_MODEL_USER_PERSONAL_CV_REQUIRED = "validation.model.user.personalCV.required";
	public static final String VALIDATION_MODEL_USER_BUSINESS_TYPE = "validation.model.user.businessType.required";
	public static final String VALIDATION_MODEL_USER_BUSINESS_SPHERE = "validation.model.user.businessSphere.required";
	public static final String VALIDATION_MODEL_TEST_QUESTION_ANSWER_REQUIRED = "validation.model.test.question.answer.required";
	public static final String VALIDATION_MODEL_TEST_QUESTION_INNER_QUESTION_ANSWER_REQUIRED = "validation.model.test.question.innerQuestion.answer.required";

	public static final String VALIDATION_LOGIN_CONTROLLER_INCORRECT_LOGIN_OR_PASSWORD = "validation.loginController.incorrectLoginOrPassword";
	public static final String VALIDATION_LOGIN_CONTROLLER_APPROVE_REGISTRATION = "validation.loginController.approveRegistration";
	public static final String VALIDATION_LOGIN_CONTROLLER_NONEXISTENT_USER = "validation.loginController.nonexistentUser";

	public static final String MESSAGE_USER_CONTROLLER_REQUEST_SENT = "message.userController.requestSent";
	public static final String MESSAGE_GROUP_CONTROLLER_INVITATION_SENT = "message.groupController.invitationSent";
	
	public static final String MESSAGES_PROJECT_PHASE = "models.ProjectPhase.name.";
	public static final String MESSAGES_BUSINESS_TYPE = "models.bType.name.";
	public static final String MESSAGES_SPHERE_NAME = "models.bSphere.name.";
	public static final String MESSAGES_COUNTRY_NAME = "models.country.name.";
	public static final String MESSAGES_USER_LEVEL = "models.userLevel.userLevel.";
	
	public static final String SLASH = "/";
	public static final String UNDERLINING = "_";
	public static final String BLANKSPACE = " ";

	public static final String MODELS_COUNTRY_NAME_PREFIX = "models.country.name.";

	public static final String ADMIN_EMAIL = "admin@admin.com";
	public static final String ADMIN_PATH_STARTS_WITH = "admin/";
	public static final String ADMIN_PATH = "/admin";
	public static final String SECOND_TEST_PATH = "/secondTest/";
	public static final String BLANK_FORM_PATH = "/blankform/";
	public static final String CITY_PATTERN = "[А-Яа-я\\-\\s]+";
	
	public static final String CACHE_USERS_COUNT = "cacheUsersCount";
	public static final String CACHE_COMMANDS_COUNT = "cacheCommandsCount";
	
	public static final int SEARCH_COUNT_ON_PAGE = 10;
    public static final Integer GROUP_TOPICS_PAGE_SIZE = 10;

    public static final String PARAMS_PAGE = "page";
}
