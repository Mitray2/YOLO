package utils;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.annotations.Expose;

public class AjaxResult {

	@Expose
	private Map<String, Object> params;

	public AjaxResult() {
		params = new HashMap<String, Object>();
	}

	public void addParameter(String key, Object object) {
		assert key != null;
		assert object != null;
		params.put(key, object);
	}

	public Map<String, Object> getParams() {
		return params;
	}

}
