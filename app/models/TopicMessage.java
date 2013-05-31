package models;

import play.data.validation.MaxSize;
import play.db.jpa.Model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
public class TopicMessage extends Model {

	@Lob
	@MaxSize(value = 2000)
	public String text;

	public Date createDate;

	@ManyToOne
	public User from;

    @ManyToOne
	public Topic topic;

    @JoinTable(
            name = "LikesTopicMessage",
            joinColumns = @JoinColumn(name = "msg_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"),
            uniqueConstraints = @UniqueConstraint(
                columnNames = { "user_id", "msg_id" }
            )
    )
    @ManyToMany
    public List<User> usersLiked;
}
