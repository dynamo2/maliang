package com.maliang.core.exception;

import java.util.Map;

import com.maliang.core.arithmetic.AE;
import com.maliang.core.arithmetic.function.Function;

public class ReturnException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	private final Function function;
	
	public ReturnException(Function fun){
		super(fun.getExpression());
		this.function = fun;
	}
	
	public Object excute(Map<String,Object> params) {
		System.out.println(" return es : " + this.function.expression);
		return this.function.executeExpression(params);
	}
	
	public static void main(String[] args) {
		String s = "{list:['111','222','3333']}";
		Map<String,Object> params = (Map<String,Object>)AE.execute(s);
		
		s = "if(true) {{h:22,c2:i.set(99),c:44,g:55,p:return(i)}}else {return('bbbbb')}";
		
		Object val = AE.execute(s,params);
		System.out.println("---- val : " + val);
		
		s = "";
	}
}
