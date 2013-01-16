package modelDTO;

import java.io.Serializable;

import models.BSphere;
import models.BType;
import models.Communication;
import models.Country;
import models.Finance;
import models.IT;
import models.Idealize;
import models.Legal;
import models.Management;
import models.Marketing;
import models.OtherSkill;
import models.Pragmatica;
import models.ProjectPhase;
import models.Trade;

public class CommandDTO implements Serializable {

	public Long id;

	public String name;

	public String city;

	public Country country;

	public String description;

	public ProjectPhase phase;

	public boolean isVacancy;
	
	public boolean global;

	public BType type;

	public BSphere sphere;

	public Marketing marketing;

	public Legal legal;

	public Trade sale;

	public Finance finance;

	public IT programming;

	public Management management;

	public OtherSkill other;

	public Pragmatica pragmatica;

	public Idealize idealize;

	public Communication communication;

}
