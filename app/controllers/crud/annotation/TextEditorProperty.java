package controllers.crud.annotation;

public class TextEditorProperty {
	
	private Integer width;
	private Integer height;
	
	public TextEditorProperty(Integer width, Integer height){
		super();
		this.height = height;
		this.width = width;
	}

	public Integer getWidth() {
		return width;
	}

	public Integer getHeight() {
		return height;
	}
	
}
