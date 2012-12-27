package models;

import static utils.ApplicationConstants.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Formula;

import play.cache.EhCacheImpl;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.JPABase;
import play.db.jpa.Model;

@Entity
public class Command extends Model {

	@MaxSize(value = 256)
	public String name;

	@OneToMany(mappedBy = "command", cascade = CascadeType.PERSIST)
	public List<User> users;

	@ManyToMany(mappedBy = "commandsForAprove", cascade = CascadeType.PERSIST)
	public List<User> usersForInvite = new ArrayList<User>();

	@OneToMany(mappedBy = "commandToInvite", cascade = CascadeType.PERSIST)
	public List<User> usersForAprove;

	@Formula("(select count(*) from User as u where u.command_id = id)")
	public int countUser;

	@Formula("(select avg(u.age) from User as u where u.command_id = id)")
	public Integer middleAge;

	@OneToOne
	public User founderUser;

	@OneToMany(cascade = { CascadeType.REMOVE })
	public List<Topic> topics;

	@Required
	@ManyToOne(cascade = { CascadeType.PERSIST })
	public Country country;

	public String city;

	@Lob
	@MaxSize(value = 2000)
	public String description;

	@ManyToOne(cascade = { CascadeType.PERSIST })
	public ProjectPhase phase;

	public boolean isVacancy;

	@ManyToOne(cascade = { CascadeType.PERSIST })
	public BType type;

	@ManyToOne(cascade = { CascadeType.PERSIST })
	public BSphere sphere;

	@ManyToOne(cascade = { CascadeType.ALL })
	public Marketing marketing;

	@ManyToOne(cascade = { CascadeType.ALL })
	public Trade trade;

	@ManyToOne(cascade = { CascadeType.ALL })
	public Management management;

	@ManyToOne(cascade = { CascadeType.ALL })
	public Finance finance;

	@ManyToOne(cascade = { CascadeType.ALL })
	public Legal legal;

	@ManyToOne(cascade = { CascadeType.ALL })
	public IT programming;

	@ManyToOne(cascade = { CascadeType.ALL })
	public OtherSkill otherSkill;

	public Date regDate;

	@ManyToOne(cascade = { CascadeType.ALL })
	public Pragmatica pragmatica;

	@ManyToOne(cascade = { CascadeType.ALL })
	public Idealize idealize;

	@ManyToOne(cascade = { CascadeType.ALL })
	public Communication communication;

	@Formula("(select avg(u.businessman) from User as u where u.command_id = id)")
	public Integer businessman;

	@Formula("(select max(u.pragmatist) from User as u where u.command_id = id)")
	public Integer pragmatist;

	@Formula("(select max(u.communicant) from User as u where u.command_id = id)")
	public Integer communicant;

	@Formula("(select max(u.idealist) from User as u where u.command_id = id)")
	public Integer idealist;

	public Pragmatica getPragmatica() {
		return pragmatica;
	}

	public void setPragmatica(Pragmatica pragmatica) {
		this.pragmatica = pragmatica;
	}

	public Idealize getIdealize() {
		return idealize;
	}

	public void setIdealize(Idealize idealize) {
		this.idealize = idealize;
	}

	public Communication getCommunication() {
		return communication;
	}

	public void setCommunication(Communication communication) {
		this.communication = communication;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ProjectPhase getPhase() {
		return phase;
	}

	public void setPhase(ProjectPhase phase) {
		this.phase = phase;
	}

	public boolean isVacancy() {
		return isVacancy;
	}

	public void setVacancy(boolean isVacancy) {
		this.isVacancy = isVacancy;
	}

	public BType getType() {
		return type;
	}

	public void setType(BType type) {
		this.type = type;
	}

	public BSphere getSphere() {
		return sphere;
	}

	public void setSphere(BSphere sphere) {
		this.sphere = sphere;
	}

	public Marketing getMarketing() {
		return marketing;
	}

	public void setMarketing(Marketing marketing) {
		this.marketing = marketing;
	}

	public Trade getTrade() {
		return trade;
	}

	public void setTrade(Trade trade) {
		this.trade = trade;
	}

	public Management getManagement() {
		return management;
	}

	public void setManagement(Management management) {
		this.management = management;
	}

	public Finance getFinance() {
		return finance;
	}

	public void setFinance(Finance finance) {
		this.finance = finance;
	}

	public Legal getLegal() {
		return legal;
	}

	public void setLegal(Legal legal) {
		this.legal = legal;
	}

	public IT getProgramming() {
		return programming;
	}

	public void setProgramming(IT programming) {
		this.programming = programming;
	}

	public OtherSkill getOtherSkill() {
		return otherSkill;
	}

	public void setOtherSkill(OtherSkill otherSkill) {
		this.otherSkill = otherSkill;
	}

	public Date getRegDate() {
		return regDate;
	}

	public void setRegDate(Date regDate) {
		this.regDate = regDate;
	}

	public List<User> getUsersForAprove() {
		return usersForAprove;
	}

	public void setUsersForAprove(List<User> usersForAprove) {
		this.usersForAprove = usersForAprove;
	}
	
	@Override
	public <T extends JPABase> T delete() {
		EhCacheImpl.getInstance().delete(CACHE_COMMANDS_COUNT);
		return super.delete();
	}
	
	@Override
	public <T extends JPABase> T save() {
		EhCacheImpl.getInstance().delete(CACHE_COMMANDS_COUNT);
		return super.save();
	}

}
