package com.maliang.core.arithmetic.function;

import java.util.Map;

public class IsNull {
	public static Boolean execute(Function function,Map<String,Object> params){
		Object obj = (Object)function.executeExpression(params);
		return obj == null;
	}
}
