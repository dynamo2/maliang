package com.maliang.core.arithmetic.calculator;

import com.maliang.core.arithmetic.node.Operator;


public class IntegerCalculator {
	public static Integer calculate(int x,int y,Operator operator){
		if(operator.isPlus()){
			return plus(x,y);
		}
		
		if(operator.isSubstruction()){
			return substruction(x,y);
		}
		
		if(operator.isMultiplication()){
			return multiplication(x, y);
		}
		
		if(operator.isDivision()){
			return division(x,y);
		}
		
		return null;
	}
	
	private static int plus(int x,int y){
		return x+y;
	}
	
	private static int substruction(int x,int y){
		return x-y;
	}
	
	private static int multiplication(int x,int y){
		return x*y;
	}
	
	private static int division(int x,int y){
		return x/y;
	}
}
