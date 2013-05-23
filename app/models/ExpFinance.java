package models;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

@Entity
public class ExpFinance extends Model {

	@ManyToOne(cascade = CascadeType.PERSIST)
	public UserLevel level;

	@Required
	@Lob
	@MaxSize(value = 2000)
	public String description;

	public UserLevel getLevel() {
		return level;
	}

	public void setLevel(UserLevel level) {
		this.level = level;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

    public ExpFinance() {
    }

    public ExpFinance(UserLevel level) {
        this.level = level;
    }

}
