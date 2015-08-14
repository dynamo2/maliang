package com.maliang.core.arithmetic.function;

import java.util.Map;

public class NotNull {
	public static Boolean execute(Function function,Map<String,Object> params){
		Object obj = (Object)function.executeExpression(params);
		
		if(obj == null)return false;
		
		return true;
	}
}
