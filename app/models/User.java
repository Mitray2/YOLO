/**
 * 
 */
package models;

import java.util.ArrayList;
import static utils.ApplicationConstants.*;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import play.cache.EhCacheImpl;
import play.data.validation.Email;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.JPABase;
import play.db.jpa.Model;

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
	
	public Boolean haveAvatar = false;
	
	public Date lastSeen;
	
	@Override
	public <T extends JPABase> T delete() {
		EhCacheImpl.getInstance().delete(CACHE_USERS_COUNT);
		return super.delete();
	}
	
	@Override
	public <T extends JPABase> T save() {
		EhCacheImpl.getInstance().delete(CACHE_USERS_COUNT);
		return super.save();
	}
	
}
