package com.maliang.core.arithmetic.function;

import java.util.Collection;
import java.util.Map;

import com.maliang.core.arithmetic.ArithmeticExpression;

public class ListFunction {
	public static Object execute(Function function ,Map<String,Object> params){
		Object expressionData = function.executeExpression(params);
		Object keyData = ArithmeticExpression.execute(function.getKey(), params);
		if(keyData == null)return null;
		
		if(!(expressionData instanceof Integer)){
			throw new RuntimeException("Error array index");
		}
		
		int index = (Integer)expressionData;
		if(keyData instanceof Collection){
			keyData = ((Collection)keyData).toArray();
		}
		
		if(!(keyData instanceof Object[])){
			throw new RuntimeException("Error type");
		}
		
		if(index < 0 && index >= ((Object[])keyData).length){
			throw new RuntimeException(index+" is out of array");
		}
		
		return ((Object[])keyData)[index];
	}
}
