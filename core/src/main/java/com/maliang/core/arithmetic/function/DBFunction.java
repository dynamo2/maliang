package com.maliang.core.arithmetic.function;

import java.util.Map;

import com.maliang.core.service.CollectionService;

public class DBFunction {
	public static Object execute(Function function,Map<String,Object> params){
		String[] keys = function.getKey().split("\\.");
		
		String collection = keys[1];
		String method = keys[2];
		
		Object value = function.executeExpression(params);
		return new CollectionService(collection).invoke(method, value);
	}
}
