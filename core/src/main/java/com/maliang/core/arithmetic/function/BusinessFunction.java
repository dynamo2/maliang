package com.maliang.core.arithmetic.function;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.maliang.core.exception.TurnToPage;
import com.maliang.core.model.Workflow;
import com.maliang.core.service.BusinessService;
import com.maliang.core.util.SessionUtil;

public class BusinessFunction {
	final static BusinessService service = new BusinessService();
	
	public static String toPage(Function function,Map<String,Object> params){
		Object value = function.executeExpression(params);
		if(params == null)params = new HashMap<String,Object>();
		
		if(value != null && value instanceof Map){
			params.putAll((Map<String,Object>)value);
		}
		//throw new TurnToPage(service.business(params));
		throw new TurnToPage(params);
	}
	
	public static List<Map<String,Object>> businesses(Function function,Map<String,Object> params){
		return service.businesses();
	}
	
	public static Map<String,Object> flow(Function function,Map<String,Object> params){
		Workflow flow = SessionUtil.getFlow();
		
		Map<String,Object> val = new LinkedHashMap<String,Object>();
		val.put("id",flow.getId().toString());
		val.put("name",flow.getName());
		val.put("step",flow.getStep());
		
		return val;
	}
}
