package com.maliang.core.arithmetic.function;

import java.util.List;
import java.util.Map;

import com.maliang.core.arithmetic.ArithmeticExpression;
import com.maliang.core.exception.TianmaException;

public class Check {
	@SuppressWarnings("unchecked")
	public static Object execute(Function function,Map<String,Object> params){
		List<Object> checkList = (List<Object>)function.executeExpression(params);
		Object result = checkList.get(0);
		if(result != null && result instanceof Boolean && (Boolean)result){
			return true;
		}
		
		String errMsg = "Check error for "+function.getExpression();
		if(checkList.size() >= 2){
			errMsg = (String)checkList.get(1);
		}
		
		throw new TianmaException(errMsg);
	}
	
	public static void main(String[] args) {
		String s = "[user.pwd = password,'error message']";
		
//		List checkList = (List)ArithmeticExpression.execute(s, null);
//		
//		System.out.println(checkList);
//		System.out.println(checkList.get(0) instanceof Boolean);
		
		s = "db.User.search()";
		Object u = ArithmeticExpression.execute(s, null);
		System.out.println(u);
	}
}
