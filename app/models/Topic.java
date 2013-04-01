package models;

import play.data.validation.MaxSize;
import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import java.util.Date;
import java.util.List;

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

	@OneToMany(fetch = FetchType.LAZY)
	public List<TopicMessage> msg;

    public Topic() {
    }

    public Topic(boolean mainTopic, boolean publicTopic, Long groupId) {
        this.mainTopic = mainTopic;
        this.publicTopic = publicTopic;
        this.groupId = groupId;
    }
}
