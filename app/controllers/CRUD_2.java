package controllers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.User;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import play.Logger;
import play.Play;
import play.data.binding.Binder;
import play.data.validation.MaxSize;
import play.data.validation.Password;
import play.data.validation.Required;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Router;
import play.utils.Java;
import utils.SessionHelper;
import controllers.crud.annotation.ChoiceFilter;
import controllers.crud.annotation.ChoiceItem;
import controllers.crud.annotation.ChoiceList;
import controllers.crud.annotation.DateTime;
import controllers.crud.annotation.HTML;
import controllers.crud.annotation.Readonly;
import controllers.crud.annotation.TextEditor;
import controllers.crud.annotation.TextEditorProperty;

public abstract class CRUD_2 extends Controller {

	@Before(priority = 1)
	public static void checkSecutiry() {
		User currentUser = SessionHelper.getCurrentUser(session);
		if (currentUser == null) {
			CommonController.error(CommonController.ERROR_SECURITY);
		}
		// enable posts for admin
		if (currentUser.role.equals(User.ROLE_ADMIN)) {
			if (Http.Request.current().path.equals("/admin")) {
				return;
			}
			if (Http.Request.current().path.startsWith("/admin/post"))
				return;
			else
				// redirect(request.getBase() + "/admin"); // this row is block
				// all actions wich is not post Action
				return;
		} else {
			CommonController.error(CommonController.ERROR_SECURITY);
		}
	}

	@Before(priority = 0)
	public static void addType() throws Exception {
		ObjectType type = ObjectType.get(getControllerClass());
		renderArgs.put("type", type);
	}

	public static void index() {
		// User currentUser = SessionHelper.getCurrentUser(session);
		// if (currentUser != null &&
		// (currentUser.role.equals(User.ROLE_ALIEN_AUTHOR) ||
		// currentUser.role.equals(User.ROLE_COACHMEN_AUTHOR) ||
		// currentUser.role.equals(User.ROLE_ADMIN))) {
		// if (getControllerClass() == CRUD_2.class) {
		// forbidden();
		// }
		render("CRUD_2/index.html");
		// } else {
		// Common.error(Common.ERROR_SECURITY);
		// }
	}

	public static void list(int page, String search, String searchFields, String orderBy, String order) {
		// User currentUser = SessionHelper.getCurrentUser(session);
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		if (page < 1) {
			page = 1;
		}
		String where = (String) request.args.get("where");
		// if (currentUser != null && !currentUser.role.equals(User.ROLE_ADMIN))
		// {
		// if (type.modelName.equals("Post")) {
		// if (where != null && where.length() != 0) {
		// where += " and ";
		// } else {
		// where = "";
		// }
		// where += " author = " + currentUser.id;
		// }
		// }
		List<Model> objects = type.findPage(page, search, searchFields, orderBy, order, where);
		Long count = type.count(search, searchFields, where);
		Long totalCount = type.count(null, null, where);
		try {
			render(type, objects, count, totalCount, page, orderBy, order);
		} catch (TemplateNotFoundException e) {
			render("CRUD_2/list.html", type, objects, count, totalCount, page, orderBy, order);
		}
	}

	public static void show(String id) {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Model object = type.findById(id);
		notFoundIfNull(object);
		try {
			render(type, object);
		} catch (TemplateNotFoundException e) {
			render("CRUD_2/show.html", type, object);
		}
	}

	@SuppressWarnings("deprecation")
	public static void attachment(String id, String field) throws Exception {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Model object = type.findById(id);
		notFoundIfNull(object);
		Object att = object.getClass().getField(field).get(object);
		if (att instanceof Model.BinaryField) {
			Model.BinaryField attachment = (Model.BinaryField) att;
			if (attachment == null || !attachment.exists()) {
				notFound();
			}
			response.contentType = attachment.type();
			renderBinary(attachment.get(), attachment.length());
		}
		// DEPRECATED
		if (att instanceof play.db.jpa.FileAttachment) {
			play.db.jpa.FileAttachment attachment = (play.db.jpa.FileAttachment) att;
			if (attachment == null || !attachment.exists()) {
				notFound();
			}
			renderBinary(attachment.get(), attachment.filename);
		}
		notFound();
	}

	public static void save(String id) throws Exception {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Model object = type.findById(id);
		notFoundIfNull(object);
		Binder.bind(object, "object", params.all());
		validation.valid(object);
		if (validation.hasErrors()) {
			renderArgs.put("error", Messages.get("crud.hasErrors"));
			try {
				render(request.controller.replace(".", "/") + "/show.html", type, object);
			} catch (TemplateNotFoundException e) {
				render("CRUD_2/show.html", type, object);
			}
		}
		object._save();
		flash.success(Messages.get("crud.saved", type.modelName));
		if (params.get("_save") != null) {
			redirect(request.controller + ".list");
		}
		redirect(request.controller + ".show", object._key());
	}

	public static void blank() throws Exception {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Constructor<?> constructor = type.entityClass.getDeclaredConstructor();
		constructor.setAccessible(true);
		Model object = (Model) constructor.newInstance();
		try {
			render(type, object);
		} catch (TemplateNotFoundException e) {
			render("CRUD_2/blank.html", type, object);
		}
	}

	public static void create() throws Exception {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Constructor<?> constructor = type.entityClass.getDeclaredConstructor();
		constructor.setAccessible(true);
		Model object = (Model) constructor.newInstance();
		Binder.bind(object, "object", params.all());
		validation.valid(object);
		if (validation.hasErrors()) {
			renderArgs.put("error", Messages.get("crud.hasErrors"));
			try {
				render(request.controller.replace(".", "/") + "/blank.html", type, object);
			} catch (TemplateNotFoundException e) {
				render("CRUD_2/blank.html", type, object);
			}
		}
		object._save();
		flash.success(Messages.get("crud.created", type.modelName));
		if (params.get("_save") != null) {
			redirect(request.controller + ".list");
		}
		if (params.get("_saveAndAddAnother") != null) {
			redirect(request.controller + ".blank");
		}
		redirect(request.controller + ".show", object._key());
	}

	public static void delete(String id) {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Model object = type.findById(id);
		notFoundIfNull(object);
		try {
			object._delete();
		} catch (Exception e) {
			flash.error(Messages.get("crud.delete.error", type.modelName));
			redirect(request.controller + ".show", object._key());
		}
		flash.success(Messages.get("crud.deleted", type.modelName));
		redirect(request.controller + ".list");
	}

	protected static ObjectType createObjectType(Class<? extends Model> entityClass) {
		return new ObjectType(entityClass);
	}

	// ~~~~~~~~~~~~~
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface For {
		Class<? extends Model> value();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface Exclude {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface Hidden {
	}

	// ~~~~~~~~~~~~~
	static int getPageSize() {
		return Integer.parseInt(Play.configuration.getProperty("crud.pageSize", "30"));
	}

	public static class ObjectType implements Comparable<ObjectType> {

		public Class<? extends Controller> controllerClass;
		public Class<? extends Model> entityClass;
		public String name;
		public String modelName;
		public String controllerName;
		public String keyName;

		public ObjectType(Class<? extends Model> modelClass) {
			this.modelName = modelClass.getSimpleName();
			this.entityClass = modelClass;
			this.keyName = Model.Manager.factoryFor(entityClass).keyName();
		}

		@SuppressWarnings("unchecked")
		public ObjectType(String modelClass) throws ClassNotFoundException {
			this((Class<? extends Model>) Play.classloader.loadClass(modelClass));
		}

		public static ObjectType forClass(String modelClass) throws ClassNotFoundException {
			return new ObjectType(modelClass);
		}

		public static ObjectType get(Class<? extends Controller> controllerClass) {
			Class<? extends Model> entityClass = getEntityClassForController(controllerClass);
			if (entityClass == null || !Model.class.isAssignableFrom(entityClass)) {
				return null;
			}
			ObjectType type;
			try {
				type = (ObjectType) Java.invokeStaticOrParent(controllerClass, "createObjectType", entityClass);
			} catch (Exception e) {
				Logger.error(e, "Couldn't create an ObjectType. Use default one.");
				type = new ObjectType(entityClass);
			}
			type.name = controllerClass.getSimpleName().replace("$", "");
			type.controllerName = controllerClass.getSimpleName().toLowerCase().replace("$", "");
			type.controllerClass = controllerClass;
			return type;
		}

		@SuppressWarnings("unchecked")
		public static Class<? extends Model> getEntityClassForController(Class<? extends Controller> controllerClass) {
			if (controllerClass.isAnnotationPresent(For.class)) {
				return ((For) (controllerClass.getAnnotation(For.class))).value();
			}
			for (Type it : controllerClass.getGenericInterfaces()) {
				if (it instanceof ParameterizedType) {
					ParameterizedType type = (ParameterizedType) it;
					if (((Class<?>) type.getRawType()).getSimpleName().equals("CRUDWrapper")) {
						return (Class<? extends Model>) type.getActualTypeArguments()[0];
					}
				}
			}
			String name = controllerClass.getSimpleName().replace("$", "");
			name = "models." + name.substring(0, name.length() - 1);
			try {
				return (Class<? extends Model>) Play.classloader.loadClass(name);
			} catch (ClassNotFoundException e) {
				return null;
			}
		}

		public Object getListAction() {
			return Router.reverse(controllerClass.getName().replace("$", "") + ".list");
		}

		public Object getBlankAction() {
			return Router.reverse(controllerClass.getName().replace("$", "") + ".blank");
		}

		public Long count(String search, String searchFields, String where) {
			return Model.Manager.factoryFor(entityClass).count(searchFields == null ? new ArrayList<String>() : Arrays.asList(searchFields.split("[ ]")), search, where);
		}

		@SuppressWarnings("unchecked")
		public List<Model> findPage(int page, String search, String searchFields, String orderBy, String order, String where) {
			return Model.Manager.factoryFor(entityClass).fetch((page - 1) * getPageSize(), getPageSize(), orderBy, order,
					searchFields == null ? new ArrayList<String>() : Arrays.asList(searchFields.split("[ ]")), search, where);
		}

		public Model findById(Object id) {
			if (id == null)
				return null;
			return Model.Manager.factoryFor(entityClass).findById(id);
		}

		public List<ObjectField> getFields() {
			List<ObjectField> fields = new ArrayList<ObjectField>();
			List<ObjectField> hiddenFields = new ArrayList<ObjectField>();
			for (Model.Property f : Model.Manager.factoryFor(entityClass).listProperties()) {
				ObjectField of = new ObjectField(f);
				if (of.type != null) {
					if (of.type.equals("hidden")) {
						hiddenFields.add(of);
					} else {
						fields.add(of);
					}
				}
			}

			hiddenFields.addAll(fields);
			return hiddenFields;
		}

		public ObjectField getField(String name) {
			for (ObjectField field : getFields()) {
				if (field.name.equals(name)) {
					return field;
				}
			}
			return null;
		}

		public int compareTo(ObjectType other) {
			return modelName.compareTo(other.modelName);
		}

		@Override
		public String toString() {
			return modelName;
		}

		public static class ObjectField {

			private Model.Property property;
			public String type = "unknown";
			public String name;
			public boolean multiple;
			public boolean required;
			public List<ChoiceItem> staticChoiceItems;
			public TextEditorProperty textEditorProperty;

			@SuppressWarnings("deprecation")
			public ObjectField(Model.Property property) {
				Field field = property.field;
				this.property = property;
				// User currentUser = SessionHelper.getCurrentUser(session);
				if (CharSequence.class.isAssignableFrom(field.getType())) {
					type = "text";
					if (field.isAnnotationPresent(MaxSize.class)) {
						int maxSize = field.getAnnotation(MaxSize.class).value();
						if (maxSize > 100) {
							type = "longtext";
						}
					}
					if (field.isAnnotationPresent(TextEditor.class)) {
						type = "texteditor";
						int width = field.getAnnotation(TextEditor.class).width();
						int height = field.getAnnotation(TextEditor.class).height();
						this.textEditorProperty = new TextEditorProperty(width != 0 ? width : null, height != 0 ? height : null);
					}
					if (field.isAnnotationPresent(Password.class)) {
						type = "password";
					}
				}
				if (Number.class.isAssignableFrom(field.getType()) || field.getType().equals(double.class) || field.getType().equals(int.class)
						|| field.getType().equals(long.class)) {
					type = "number";
				}
				if (Boolean.class.isAssignableFrom(field.getType()) || field.getType().equals(boolean.class)) {
					type = "boolean";
				}
				if (Date.class.isAssignableFrom(field.getType())) {
					type = "date";
				}
				if (property.isRelation) {
					type = "relation";
				}
				if (property.isMultiple) {
					multiple = true;
				}
				if (Model.BinaryField.class.isAssignableFrom(field.getType()) || /**
				 * 
				 * DEPRECATED
				 **/
				play.db.jpa.FileAttachment.class.isAssignableFrom(field.getType())) {
					type = "binary";
				}
				if (field.getType().isEnum()) {
					type = "enum";
				}
				if (property.isGenerated) {
					type = null;
				}
				if (field.isAnnotationPresent(Required.class)) {
					required = true;
				}
				if (field.isAnnotationPresent(Readonly.class) && field.getAnnotation(Readonly.class).readonlyForAuthor() && field.getAnnotation(Readonly.class).readonlyForAdmin()
						&& (type.equals("number") || type.equals("text") || type.equals("date"))) {
					type = "disabledTextField";
				}
				if (field.isAnnotationPresent(Readonly.class) && field.getAnnotation(Readonly.class).readonlyForAuthor() && field.getAnnotation(Readonly.class).readonlyForAdmin()
						&& type.equals("boolean")) {
					type = "disabledCheckBox";
				}
				if (field.isAnnotationPresent(Readonly.class) && field.getAnnotation(Readonly.class).readonlyForAuthor() && field.getAnnotation(Readonly.class).readonlyForAdmin()
						&& type.equals("longtext")) {
					type = "disabledTextArea";
				}
				if (field.isAnnotationPresent(Hidden.class)) {
					type = "hidden";
				}
				if (field.isAnnotationPresent(Exclude.class)) {
					type = null;
				}
				if (java.lang.reflect.Modifier.isFinal(field.getModifiers())) {
					type = null;
				}
				if (field.isAnnotationPresent(ChoiceList.class)) {
					type = "staticChoice";
					this.staticChoiceItems = ChoiceItem.parseItems(field.getAnnotation(ChoiceList.class).choices());
				}
				if (field.isAnnotationPresent(HTML.class)) {
					type = "html";
				}
				if (field.isAnnotationPresent(Readonly.class)) {
					if (field.getAnnotation(Readonly.class).readonlyForAuthor()
					// && (currentUser.role.equals(User.ROLE_ALIEN_AUTHOR) ||
					// currentUser.role.equals(User.ROLE_COACHMEN_AUTHOR))
					) {
						type = "readonly";
					}
				}
				if (field.isAnnotationPresent(DateTime.class)) {
					type = "dateTime";
					if (field.isAnnotationPresent(Readonly.class)) {
						if (field.getAnnotation(Readonly.class).readonlyForAuthor()
						// && (currentUser.role.equals(User.ROLE_ALIEN_AUTHOR)
						// ||
						// currentUser.role.equals(User.ROLE_COACHMEN_AUTHOR))
						) {
							type = "dateTimereadonly";
						}
					}
				}
				if (field.isAnnotationPresent(Readonly.class)) {
					if (field.getAnnotation(Readonly.class).hiddenForAuthor()
					// && (currentUser.role.equals(User.ROLE_ALIEN_AUTHOR) ||
					// currentUser.role.equals(User.ROLE_COACHMEN_AUTHOR))
					) {
						type = "hidden";
					}
				}
				name = field.getName();
			}

			public List<Object> getChoices() {
				Field field = property.field;
				List<Object> result = property.choices.list();
				if (field.isAnnotationPresent(ChoiceFilter.class)) {
					ChoiceFilter cf = field.getAnnotation(ChoiceFilter.class);
					Map<String, Condition> conditions = parseConditions(cf.value());
					List<Object> filteredResult = new ArrayList<Object>();
					for (Map.Entry<String, Condition> condition : conditions.entrySet()) {
						List<Object> filteredResults = (List<Object>) CollectionUtils.select(result, new ConditionFilterPredicate(condition.getKey(), condition.getValue()));
						for (Object filtered : filteredResults) {
							if (!filteredResult.contains(filtered))
								filteredResult.add(filtered);
						}
					}
					result = filteredResult;
				}
				return result;
			}

			private Map<String, Condition> parseConditions(String value) {
				// need to be written if any other conditions appear
				String[] orConditions = value.split("\\)or\\(");
				Map<String, Condition> result = new HashMap<String, Condition>(orConditions.length);
				for (String condition : orConditions) {
					if (condition.charAt(0) == '(') {
						condition = condition.substring(1);
					}
					if (condition.charAt(condition.length() - 1) == ')') {
						condition = condition.substring(0, condition.length() - 1);
					}
					String[] puttedValue = condition.split("\\^");
					result.put(puttedValue[0], new Condition(Arrays.asList(puttedValue[1].split("\\|")), Boolean.parseBoolean(puttedValue[2])));
				}
				return result;
			}

			private class ConditionFilterPredicate implements Predicate {

				private String fieldName;
				private Condition condition;

				ConditionFilterPredicate(String fieldName, Condition condition) {
					this.fieldName = fieldName;
					this.condition = condition;
				}

				@Override
				public boolean evaluate(Object object) {
					try {
						Object value = object.getClass().getDeclaredField(fieldName).get(object);
						return value != null ? condition.isTrue ? condition.values.contains(value.toString()) : !condition.values.contains(value.toString()) : false;
					} catch (Exception e) {
						e.printStackTrace();
					}
					return false;
				}
			}

			private class Condition {

				private boolean isTrue;

				private List<String> values;

				Condition(List<String> values, boolean isTrue) {
					this.values = values;
					this.isTrue = isTrue;
				}

			}

		}
	}
	// for tests
	// public static void main(String[] args) {
	//
	// }
}
