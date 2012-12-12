package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.OneToMany;

import play.data.validation.MaxSize;
import play.db.jpa.Model;

@Entity
public class Topic extends Model {

	public boolean mainTopic = false;

	public boolean publicTopic;

	public String name;

	public Date lastUpdateDate;

	public Long lastUpdateUserId;

	public String lastUpdateUserName;

	public String lastUpdateUserLastName;

	public Long createdUserId;

	public String createdUserName;

	public String createdUserLastName;

	public Long groupId;

	public Date createdDateTime;
	@Lob
	@MaxSize(value = 2000)
	public String description;

	@OneToMany
	public List<TopicMessage> msg;

}
