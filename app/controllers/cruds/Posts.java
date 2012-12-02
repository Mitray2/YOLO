package controllers.cruds;

import controllers.CRUD_2;
import controllers.CRUD_2.For;
import models.Post;
import play.data.binding.Binder;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import play.i18n.Messages;
import utils.RssHelper;

import java.lang.reflect.Constructor;

@For(Post.class)
public class Posts extends CRUD_2 {

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
        afterSave((Post)object);

        flash.success(Messages.get("crud.created", type.modelName));
        if (params.get("_save") != null) {
            redirect(request.controller + ".list");
        }
        if (params.get("_saveAndAddAnother") != null) {
            redirect(request.controller + ".blank");
        }
        redirect(request.controller + ".show", object._key());
    }

    public static void save(Long id) throws Exception {
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
        afterSave((Post)object);

        flash.success(Messages.get("crud.saved", type.modelName));
        if (params.get("_save") != null) {
            redirect(request.controller + ".list");
        }
        redirect(request.controller + ".show", object._key());
    }

    private static void afterSave(Post post) {
        //rss publication
        if(post.state.equals(Post.STATE_WORK) && post.type.equals(Post.TYPE_NEWS) && !post.published) {
            RssHelper.getInstance().addNews(post, request.getBase());
        }
    }
}
