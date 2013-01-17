package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import play.data.validation.MaxSize;
import play.db.jpa.Model;

@Entity
public class CrowdConsulting extends Model{

	@Lob
	@MaxSize(value = 2000)
	public String text;

	public Date createDate;

	@ManyToOne
	public User from;
}
