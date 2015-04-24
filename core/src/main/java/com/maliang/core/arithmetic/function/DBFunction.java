package com.maliang.core.arithmetic.function;

import java.util.Map;

import com.maliang.core.service.DBService;

public class DBFunction {
	public static Object execute(Function function,Map<String,Object> params){
		String[] keys = function.getKey().split(".");
		
		String collection = keys[1];
		String method = keys[2];
		
		Object value = function.executeExpression(params);
		return new DBService(collection).invoke(method, value);
	}
}
