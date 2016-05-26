package com.maliang.core.arithmetic.function;

import java.util.Map;

import com.maliang.core.service.CollectionService;

public class DBFunction {
	public static Object execute(Function function,Map<String,Object> params){
//		String[] keys = function.getKey().split("\\.");
//
//		String collection = keys[1];
//		String method = keys[2];
//		
//		Object value = function.executeExpression(params);
//		return new CollectionService(collection).invoke(method, value);
		
		String key = function.getKey();
		int start = key.indexOf(".");
		int end = key.lastIndexOf(".");
		
		String collection = key.substring(start+1,end);
		String method = key.substring(end+1);
		
		Object value = function.executeExpression(params);
		return new CollectionService(collection).invoke(method, value);
	}
	
	public static void main(String[] args) {
		String s = "db.Account.personal_profile.address.get";
		
		System.out.println("index of : " + s.indexOf("."));
		
		int start = s.indexOf(".");
		int end = s.lastIndexOf(".");
		
		String collName = s.substring(start+1,end);
		String method = s.substring(end+1);
		
		start = collName.indexOf(".");
		String innerName = collName.substring(start+1);
		collName = collName.substring(0,start);
		
		System.out.println("collection : " + collName);
		System.out.println("method : " + method);
		System.out.println("innerName : " + innerName);
	}
}
