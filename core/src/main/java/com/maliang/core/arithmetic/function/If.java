package com.maliang.core.arithmetic.function;

import java.util.Map;

import com.maliang.core.arithmetic.ArithmeticExpression;

public class If {
	public static void main(String[] args) {
		String str = "if(2<3){product:'雪花秀',price:345.90}";
		
//		List<Map<String,Object>> products = DBData.list("Product");
//		Map<String,Object> params = new HashMap<String,Object>();
//		Map<String,Object> user = DBData.getRandom("User");
//		
//		params.put("products", products);
//		params.put("user", user);
		
		//str = "2<3";
		Object v = ArithmeticExpression.execute(str,null);
		System.out.println(v);
	}
	
	public static Object execute(Function function,Map<String,Object> params){
		Object value = function.executeExpression(params);
		if(value instanceof Boolean && (Boolean)value){
			return FunctionBody.readBody(function).execute(params);
		}
		
		return null;
	}
}
