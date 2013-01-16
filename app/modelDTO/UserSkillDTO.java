package modelDTO;

import java.io.Serializable;

import models.BSphere;
import models.BType;
import models.ExpFinance;
import models.ExpIT;
import models.ExpLegal;
import models.ExpManagement;
import models.ExpMarketing;
import models.ExpOther;
import models.ExpSale;

public class UserSkillDTO implements Serializable {

	public BType businessType;

	public BSphere businessSphere;

	public ExpMarketing expMarketing;

	public ExpSale expSale;

	public ExpManagement expManagement;

	public ExpFinance expFinance;

	public ExpLegal expLegal;

	public ExpIT expIT;

	public ExpOther expOther;

	public String personalCV;
	
	public boolean english;
}
