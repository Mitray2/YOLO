package models;

import controllers.CRUD_2.Hidden;
import controllers.crud.annotation.ChoiceList;
import controllers.crud.annotation.TextEditor;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.data.validation.URL;
import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.Lob;
import java.util.Date;

@Entity
public class Post extends Model {

    public static final Integer STATE_DRAFT = 0;
    public static final Integer STATE_WORK = 1;

    public static final Integer TYPE_NEWS = 1;

    @Required
    public Date creationDate = new Date();

    @ChoiceList(choices = "0^Черновик,1^Готово")
    public Integer state = 0;
    
    @ChoiceList(choices = "0^Русский,1^Английский")
    public Integer lang = 0;

    @Required
    public String title;

    @Lob
    @MaxSize(value = 1000)
    @Required
    public String preview;

    @URL
    public String previewImageURL;

    @Lob
    @TextEditor(width = 630)
    @Required
    public String value;

    /**
     * Used to indicate whether post was published for example in rss or not.
     */
    @Hidden
    public Boolean published = false;

    @Hidden
    public Integer type = TYPE_NEWS;
}
