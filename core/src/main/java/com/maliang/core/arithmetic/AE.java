package com.maliang.core.arithmetic;

import java.util.Map;

public class AE {
	public static Object execute(String expre){
		return ArithmeticExpression.execute(expre,null);
	}
	
	public static Object execute(String expre,Map<String,Object> params){
		return ArithmeticExpression.execute(expre,params);
	}
}
