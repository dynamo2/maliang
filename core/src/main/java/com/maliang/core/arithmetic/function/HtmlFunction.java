package com.maliang.core.arithmetic.function;

import java.util.List;
import java.util.Map;

import com.maliang.core.service.HtmlService;

public class HtmlFunction {
	static HtmlService htmlService = new HtmlService();
	public static Object execute(Function function,Map<String,Object> params){
		String[] keys = function.getKey().split("\\.");
		String method = keys[keys.length - 1];
		
		String[] names = new String[keys.length -2];
		for(int i = 1; i < keys.length-1; i++){
			names[i-1] = keys[i];
		}

		Object value = function.executeExpression(params);
		if("form".equals(method)){
			Object defaultVal = null;
			List htmlSetting = null;
			if(value != null){
				if(value instanceof List){
					List formParams = (List)value;
					defaultVal = formParams.size() > 0?formParams.get(0):null;
					htmlSetting = formParams.size() > 1?(List)formParams.get(1):null;
				}else {
					defaultVal = value;
				}
			}
			return htmlService.form(names, defaultVal, htmlSetting);
		}
		
		// h.Product.info.form([dd,setting]);
		return null;
	}
}
