package com.maliang.core.arithmetic.calculator;

import com.maliang.core.arithmetic.node.Operator;


public class LogicalCalculator {
	public static boolean calculate(Object valueLeft,Object valueRight,Operator operator){
		boolean left = getBoolean(valueLeft);
		boolean right = getBoolean(valueRight);
		
		if(operator.isOr()){
			return left || right;
		}
		
		return left && right;
	}
	
	public static boolean getBoolean(Object v){
		if(v == null)return false;
		
		try {
			return (v instanceof Boolean)?(Boolean)v:new Boolean(v.toString());
		}catch(Exception e){
			return false;
		}
	}
}
