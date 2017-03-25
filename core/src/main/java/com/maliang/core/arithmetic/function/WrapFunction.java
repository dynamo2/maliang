package com.maliang.core.arithmetic.function;

import java.util.List;
import java.util.Map;

import com.maliang.core.arithmetic.ArithmeticExpression;
import com.maliang.core.arithmetic.Substring;
import com.maliang.core.service.HtmlService;

public class WrapFunction {
	static HtmlService htmlService = new HtmlService();
	public static Object execute(Function function,Map<String,Object> params){
		Object operatedObj = function.getKeyValue();
		if(operatedObj == null){
			return null;
		}
		
		System.out.println("wrap expression : " + function.expression);
		
		//if(function.expression != null)return null;
		
		Object expre = function.executeExpression(params);
		String[] wraps = new String[]{"",""};
		
		if(expre != null){
			if(expre instanceof List){
				List list = (List)expre;
				if(list.size() == 1){
					wraps[0] = list.get(0).toString();
					wraps[1] = list.get(0).toString();
				}else if(list.size() > 1) {
					wraps[0] = list.get(0).toString();
					wraps[1] = list.get(1).toString();
				}
			}else {
				wraps[0] = expre.toString();
				wraps[1] = expre.toString();
			}
		}
		
		return new StringBuffer().append(wraps[0]).append(operatedObj).append(wraps[1]).toString();
	}
	
	public static void main(String[] args) {
		String s = "'aaa\'ddd\'ccc'";
		Substring sbs = new Substring(s,'\'',0);
		
//		Object v = ArithmeticExpression.execute(s, null);
//		System.out.println("v : "+v);
	}
}
