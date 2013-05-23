package models;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

@Entity
public class ExpOther extends Model {

	@Required
	@MaxSize(value = 30)
	public String expName;

	@ManyToOne(cascade = CascadeType.PERSIST)
	public UserLevel level;

	@Required
	@Lob
	@MaxSize(value = 2000)
	public String description;

    public ExpOther() {
    }

    public ExpOther(UserLevel level) {
        this.level = level;
    }

}
