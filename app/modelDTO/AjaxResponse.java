package modelDTO;

import java.util.HashMap;
import java.util.Map;

public class AjaxResponse {

	public static final int RESULT_SUCCESS = 0;
	public static final int RESULT_ERROR = 1;
	
	public int resultCode = 0;
	
	public String errorDetails = "";
	
	public Map<String, Object> data = new HashMap<String, Object>();
	
}
