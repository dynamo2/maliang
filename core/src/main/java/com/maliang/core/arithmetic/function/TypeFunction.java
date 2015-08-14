package com.maliang.core.arithmetic.function;

import java.util.Map;

public class TypeFunction {
	public static Integer intExecute(Function function,Map<String,Object> params){
		Object value = function.executeExpression(params);
		if(value instanceof Integer){
			return (Integer)value;
		}
		
		try {
			return new Integer(value.toString());
		}catch(Exception e){
			return null;
		}
	}
	
	public static Double doubleExecute(Function function,Map<String,Object> params){
		Object value = function.executeExpression(params);
		if(value instanceof Double){
			return (Double)value;
		}
		
		try {
			return new Double(value.toString());
		}catch(Exception e){
			return null;
		}
	}
	
	public static Float floatExecute(Function function,Map<String,Object> params){
		Object value = function.executeExpression(params);
		if(value instanceof Float){
			return (Float)value;
		}
		
		try {
			return new Float(value.toString());
		}catch(Exception e){
			return null;
		}
	}
	
	public static String stringExecute(Function function,Map<String,Object> params){
		Object value = function.executeExpression(params);
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
