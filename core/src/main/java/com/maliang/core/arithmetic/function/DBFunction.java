package com.maliang.core.arithmetic.function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.maliang.core.arithmetic.AE;
import com.maliang.core.service.CollectionService;
import com.maliang.core.util.Utils;

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
		
		if(start == end){
			String method = key.substring(end+1);
			if("in".equals(method)){
				return in(function,params);
			}
			
			if("eq".equals(method)){
				return eq(function,params);
			}
			
			if("and".equals(method)){
				return and(function,params);
			}
			
			if("or".equals(method)){
				return or(function,params);
			}
			
			return null;
		}
		
		String collection = key.substring(start+1,end);
		String method = key.substring(end+1);
		
		Object value = function.executeExpression(params);
		return new CollectionService(collection).invoke(method, value);
	}
	
	private static Map in(Function function,Map<String,Object> params){
		Map map = readMapValue(function,params);
		if(map.isEmpty())return null;
		
		String key = map.keySet().iterator().next().toString();
		Object value = map.get(key);
		Object[] vals = Utils.toArray(value);
		if(Utils.isEmpty(vals))return null;
		
		List orList = new ArrayList();
		for(Object v : vals){
			if(v == null)continue;
			
			orList.add(queryMap(key,queryMap("$eq",v)));
		}
		
		if(orList.size() == 0)return null;
		return queryMap("$or",orList);
	}
	
	private static Map eq(Function function,Map<String,Object> params){
		Map map = readMapValue(function,params);
		if(map.isEmpty())return null;
		
		String key = map.keySet().iterator().next().toString();
		Object value = map.get(key);
		if(Utils.isEmpty(value))return null;
		
		return queryMap(key,queryMap("$eq",value));
	}
	
	private static Map and(Function fun,Map<String,Object> p){
		List<Object> ands = readListValue(fun,p);
		if(Utils.isEmpty(ands))return null;
		
		return queryMap("$and",ands);
	}
	
	private static Map or(Function fun,Map<String,Object> p){
		List<Object> ors = readListValue(fun,p);
		if(Utils.isEmpty(ors))return null;
		
		return queryMap("$or",ors);
	}
	
	private static Map<String,Object> queryMap(String k,Object v){
		Map<String,Object> qMap = new HashMap<String,Object>();
		qMap.put(k,v);
		
		return qMap;
	}
	
	private static List<Object> readListValue(Function function,Map<String,Object> params){
		Object value = function.executeExpression(params);
		if(value == null)return null;
		
		List<Object> list = Utils.toList(value);
		return Utils.clearNull(list);
	}
	
	private static Map<String,Object> readMapValue(Function function,Map<String,Object> params){
		Object value = function.executeExpression(params);
		if(value == null)return null;
		
		Map map = null;
		if(value instanceof Map){
			map = (Map)value;
		}
		return map;
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
		
		
		s = "{types:[1,2,3,4]}";
		Map<String,Object> params = (Map<String,Object>)AE.execute(s);
		
		s = "db.in({task.type.code:types})";
		Object v = AE.execute(s,params);
		
		System.out.println("========== v : " + v);
	}
}
