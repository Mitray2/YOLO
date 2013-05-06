/**
 * 
 */
package models;

import org.hibernate.annotations.Formula;
import play.cache.Cache;
import play.data.validation.Email;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.JPABase;
import play.db.jpa.Model;

import javax.persistence.*;
import java.util.*;

import static utils.ApplicationConstants.CACHE_USERS_COUNT;

/**
 * @author Администратор
 * 
 */
@Entity
public class User extends Model {

	public static final int ROLE_ADMIN = 0;
	public static final int ROLE_USER = 1;
	public static final int ROLE_INPERFECT_USER = 2;
	public static final int ROLE_GROUP_ADMIN = 3;
	public static final int ROLE_WITHOUT_BLANK = 4;

	@Required
	public String passwordHash;

	public String mailTicket;

	public Integer role = ROLE_INPERFECT_USER;

	@Required
	@Email
	public String email;

	@Required()
	public Date birthday;

	/**
	 * true = male false = female
	 */
	@Required
	public Boolean sex;

	public Boolean english;

	@Required
	@MaxSize(value = 30)
	public String name;

	@Required
	@MaxSize(value = 30)
	public String lastName;

	public Integer age;

	@ManyToOne(cascade = { CascadeType.PERSIST })
	public Country country;

	@Required
	@MaxSize(value = 30)
	public String city;

	@ManyToOne(cascade = { CascadeType.PERSIST })
	public BType businessType;

	@ManyToOne(cascade = { CascadeType.PERSIST })
	public BSphere businessSphere;

	@OneToOne(cascade = { CascadeType.PERSIST })
	@Required
	public ExpMarketing expMarketing;

	@OneToOne(cascade = { CascadeType.PERSIST })
	@Required
	public ExpSale expSale;

	@OneToOne(cascade = { CascadeType.PERSIST })
	@Required
	public ExpManagement expManagement;

	@OneToOne(cascade = { CascadeType.PERSIST })
	@Required
	public ExpFinance expFinance;

	@OneToOne(cascade = { CascadeType.PERSIST })
	@Required
	public ExpLegal expLegal;

	@OneToOne(cascade = { CascadeType.PERSIST })
	@Required
	public ExpIT expIT;

	@OneToOne(cascade = { CascadeType.PERSIST })
	public ExpOther expOther;

	@Required
	@Lob
	@MaxSize(value = 2000)
	public String personalCV;

	public Integer pragmatist = 0;

	public Integer communicant = 0;

	public Integer idealist = 0;

	public Integer businessman;

	public Date regDate;

	public Boolean showEmailForOthers = false;

	@ManyToOne
	public Command command;

	@ManyToOne
	public Command commandToInvite;

	@ManyToMany(cascade = { CascadeType.PERSIST })
	public List<Command> commandsForAprove = new ArrayList<Command>();

    @ManyToMany(cascade = { CascadeType.PERSIST }, fetch = FetchType.EAGER)
    public List<NotificationType> notifications = new ArrayList<NotificationType>();

    @JoinTable(
        name = "UserFavouriteTeam",
        joinColumns = @JoinColumn(name = "User_id"),
        inverseJoinColumns = @JoinColumn(name = "Team_id")
    )
    @ManyToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
    public Set<Command> favouriteTeams = new HashSet<Command>();

    @JoinTable(
        name = "UserBlacklistTeam",
        joinColumns = @JoinColumn(name = "User_id"),
        inverseJoinColumns = @JoinColumn(name = "Team_id")
    )
    @ManyToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
    public Set<Command> blacklistTeams = new HashSet<Command>();
	
	public Boolean haveAvatar = false;
	
	public Date lastSeen;
	public Date lastSeenInTeam;

	public Date lastNotified;

	public String preferredLang;

	public boolean playSounds;

	public boolean takePartInAutoTeams = true;

    @Formula("IF(TIMEDIFF(NOW(),lastSeen) < '00:10:00' ,'online',IF(TIMEDIFF(NOW(),lastSeen) < '00:20:00','away','offline'))")
    public String onlineStatus;

	@Override
	public <T extends JPABase> T delete() {
		Cache.delete(CACHE_USERS_COUNT);
		return (T) super.delete();
	}
	
	@Override
	public <T extends JPABase> T save() {
        Cache.delete(CACHE_USERS_COUNT);
		return (T) super.save();
	}

    public User() {
    }

    public User(Long id) {
        this.id = id;
    }


}
