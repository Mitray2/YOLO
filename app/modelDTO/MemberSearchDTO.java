package modelDTO;

import java.io.Serializable;

public class MemberSearchDTO implements Serializable {

	public String country;

	public String city;

	public Integer ageMin;

	public Integer ageMax;

	public String sex;
	
	public String english;

	public String inCommand;

	public String bissnessType;

	public String bissnessSphere;

	public String marketing;

	public String sale;

	public String management;

	public String finance;

	public String legal;

	public String it;

	public Integer bmanMin;

	public Integer bmanMax;

	public Integer idealMin;

	public Integer idealMax;

	public Integer comutMin;

	public Integer comutMax;

	public Integer pragmatMin;

	public Integer pragmatMax;

	public String orderBy;

	public boolean asc;

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MemberSearchDTO [country=");
		builder.append(country);
		builder.append(", city=");
		builder.append(city);
		builder.append(", ageMin=");
		builder.append(ageMin);
		builder.append(", ageMax=");
		builder.append(ageMax);
		builder.append(", sex=");
		builder.append(sex);
		builder.append(", inCommand=");
		builder.append(inCommand);
		builder.append(", bissnessType=");
		builder.append(bissnessType);
		builder.append(", bissnessSphere=");
		builder.append(bissnessSphere);
		builder.append(", marketing=");
		builder.append(marketing);
		builder.append(", sale=");
		builder.append(sale);
		builder.append(", management=");
		builder.append(management);
		builder.append(", finance=");
		builder.append(finance);
		builder.append(", legal=");
		builder.append(legal);
		builder.append(", it=");
		builder.append(it);
		builder.append(", bmanMin=");
		builder.append(bmanMin);
		builder.append(", bmanMax=");
		builder.append(bmanMax);
		builder.append(", idealMin=");
		builder.append(idealMin);
		builder.append(", idealMax=");
		builder.append(idealMax);
		builder.append(", comutMin=");
		builder.append(comutMin);
		builder.append(", comutMax=");
		builder.append(comutMax);
		builder.append(", pragmatMin=");
		builder.append(pragmatMin);
		builder.append(", pragmatMax=");
		builder.append(pragmatMax);
		builder.append(", orderBy=");
		builder.append(orderBy);
		builder.append(", asc=");
		builder.append(asc);
		builder.append("]");
		return builder.toString();
	}

}
