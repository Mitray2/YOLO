package models;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

@Entity
public class ExpLegal extends Model {

	@ManyToOne(cascade = CascadeType.PERSIST)
	public UserLevel level;

	@Required
	@Lob
	@MaxSize(value = 2000)
	public String description;

    public ExpLegal() {
    }

    public ExpLegal(UserLevel level) {
        this.level = level;
    }
}
