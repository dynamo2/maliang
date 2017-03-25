package com.maliang.core.arithmetic.function;

import java.util.Map;

import com.maliang.core.arithmetic.ArithmeticExpression;

public class UpdateFunction {
	public static Object execute(Function function,Map<String,Object> params){
		Object value = function.executeExpression(params);
		
		if(value instanceof Boolean && (Boolean)value){
			//return FunctionBody.readBody(function).execute(params);
			return ArithmeticExpression.execute(function.getBody(), params);
		}
		return null;
	}
}
