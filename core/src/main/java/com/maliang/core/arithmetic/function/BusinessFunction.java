package com.maliang.core.arithmetic.function;

import java.util.Map;

import com.maliang.core.exception.TurnToPage;
import com.maliang.core.service.BusinessService;

public class BusinessFunction {
	final static BusinessService service = new BusinessService();
	
	public static String toPage(Function function,Map<String,Object> params){
		Object value = function.executeExpression(params);
		if(value != null && value instanceof Map){
			Map<String,Object> map = (Map<String,Object>)value;

			throw new TurnToPage(service.business(map));
		}
		return null;
	}
}
