package modelDTO;

import java.io.Serializable;

public class GroupSearchDTO implements Serializable {

	@Override
	public String toString() {
		return "GroupSearchDTO [usersMin=" + usersMin + ", usersMax="
				+ usersMax + ", vacancy=" + vacancy + ", country=" + country
				+ ", city=" + city + ", avgAgeMin=" + avgAgeMin
				+ ", avgAgeMax=" + avgAgeMax + ", bissnessType=" + bissnessType
				+ ", bissnessSphere=" + bissnessSphere + ", marketing="
				+ marketing + ", sale=" + sale + ", management=" + management
				+ ", finance=" + finance + ", legal=" + legal + ", it=" + it
				+ ", bmanMin=" + bmanMin + ", bmanMax=" + bmanMax
				+ ", idealMin=" + idealMin + ", idealMax=" + idealMax
				+ ", comutMin=" + comutMin + ", comutMax=" + comutMax
				+ ", pragmatMin=" + pragmatMin + ", pragmatMax=" + pragmatMax
				+ ", orderBy=" + orderBy + ", asc=" + asc + "]";
	}

	public Integer usersMin;

	public Integer usersMax;

	public String vacancy;

	public String country;

	public String city;

	public Integer avgAgeMin;

	public Integer avgAgeMax;

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
	
	public String phase;

	public String orderBy;

	public boolean asc;
}
