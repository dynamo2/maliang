package com.maliang.core.arithmetic.function;

import java.util.Map;

import com.maliang.core.arithmetic.ArithmeticExpression;

public class ExecuteFunction {
	public static Object execute(Function function,Map<String,Object> params){
		Object obj = (Object)function.executeExpression(params);
		
		if(obj instanceof String){
			return ArithmeticExpression.execute((String)obj, params);
		}
		
		return obj;
	}
	
	public static void main(String[] args) {
		String s = "exe('db.Region.search()')";
		Object v = ArithmeticExpression.execute(s, null);
		System.out.println(v);
	}
}
