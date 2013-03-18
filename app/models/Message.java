package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import play.data.validation.MaxSize;
import play.db.jpa.Model;

@Entity
public class Message extends Model{

	@Lob
	@MaxSize(value = 2000)
	public String text;

	public Date time;

	@ManyToOne
	public User from;

	@ManyToOne
	public User to;

	public boolean isRead;

	public long deletedBy;
}
