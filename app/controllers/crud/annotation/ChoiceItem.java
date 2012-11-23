package controllers.crud.annotation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ChoiceItem {

	private Integer id;
	private String label;
	
	public ChoiceItem() {
		
	}
	
	public ChoiceItem(Integer id, String label) {
		super();
		this.id = id;
		this.label = label;
	}
	
	/**
	 * @param txt in format 3^label
	 * @return
	 */
	public static ChoiceItem parseItem(String txt) {
		return new ChoiceItem(Integer.valueOf(txt.split("\\^")[0]), txt.split("\\^")[1]);
	}
	
	/**
	 * @param txt in format 1^Новичек,2^Продвинутый,3^Эксперт
	 * @return
	 */
	public static List<ChoiceItem> parseItems(String txt) {
		List<ChoiceItem> result = new ArrayList<ChoiceItem>();
		for (String s : txt.split(",")) {
			result.add(parseItem(s));
		}
		return result;
	}
	
	public static List<ChoiceItem> parseItems(Class clazz, String fieldName) {
		String txt = "";
		for (Field f : clazz.getDeclaredFields()) {
			if (f.getName().equals(fieldName)) {
				txt = f.getAnnotation(ChoiceList.class).choices();
			}
		}
		return parseItems(txt);
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}
