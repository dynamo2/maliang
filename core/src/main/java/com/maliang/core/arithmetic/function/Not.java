package com.maliang.core.arithmetic.function;

import java.util.Map;

public class Not {
	public static Boolean execute(Function function,Map<String,Object> params){
		Object obj = (Object)function.executeExpression(params);
		
		if(obj instanceof Boolean){
			return !((Boolean)obj);
		}
		
		return false;
	}
}
