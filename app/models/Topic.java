package models;

import play.data.validation.MaxSize;
import play.db.jpa.Model;

import javax.persistence.*;
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

	//public Long groupId;
    @ManyToOne
	public Command team;

	public Date createdDateTime;

	@Lob
	@MaxSize(value = 2000)
	public String description;

	@OneToMany(mappedBy="topic", fetch = FetchType.LAZY)
	public List<TopicMessage> messages;

    public Topic() {
    }

    public Topic(String name) {
        this.name = name;
    }

    public Topic(boolean mainTopic, boolean publicTopic, Command team) {
        this.mainTopic = mainTopic;
        this.publicTopic = publicTopic;
        this.team = team;
    }
}
