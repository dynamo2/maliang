package com.maliang.core.arithmetic.function;

import java.util.Map;

import com.maliang.core.service.MapHelper;

public class TypeFunction {
	public static Integer intExecute(Function function,Map<String,Object> params){
		return intValueOf(function.executeExpression(params));
	}
	
	public static Integer intExecute(String operatedKey,Map<String,Object> params){
		return intValueOf(MapHelper.readValue(params,operatedKey));
	}
	
	public static Double doubleExecute(Function function,Map<String,Object> params){
		return doubleValueOf(function.executeExpression(params));
	}
	
	public static Double doubleExecute(String operatedKey,Map<String,Object> params){
		return doubleValueOf(MapHelper.readValue(params,operatedKey));
	}
	
	public static Float floatExecute(Function function,Map<String,Object> params){
		return floatValueOf(function.executeExpression(params));
	}
	
	public static Float floatExecute(String operatedKey,Map<String,Object> params){
		return floatValueOf(MapHelper.readValue(params,operatedKey));
	}
	
	public static String stringExecute(Function function,Map<String,Object> params){
		return stringValueOf( function.executeExpression(params));
	}
	
	public static String stringExecute(String operatedKey,Map<String,Object> params){
		return stringValueOf(MapHelper.readValue(params,operatedKey));
	}
	
	private static Integer intValueOf(Object value){
		if(value instanceof Number){
			return ((Number)value).intValue();
		}
		
		try {
			return new Integer(value.toString());
		}catch(Exception e){
			return null;
		}
	}
	
	private static Double doubleValueOf(Object value){
		if(value instanceof Number){
			return ((Number)value).doubleValue();
		}
		
		try {
			return new Double(value.toString());
		}catch(Exception e){
			return null;
		}
	}
	
	private static Float floatValueOf(Object value){
		if(value instanceof Number){
			return ((Number)value).floatValue();
		}
		
		try {
			return new Float(value.toString());
		}catch(Exception e){
			return null;
		}
	}
	
	private static String stringValueOf(Object value){
		if(value instanceof String){
			return (String)value;
		}
		
		try {
			return value.toString();
		}catch(Exception e){
			return null;
		}
	}
}
