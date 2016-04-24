package com.maliang.core.arithmetic.calculator;

import com.maliang.core.arithmetic.node.Operator;


public class DoubleCalculator {
	public static Double calculate(double x,double y,Operator operator){
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
	
	private static Double plus(double x,double y){
		return x+y;
	}

	private static Double substruction(double x,double y){
		return x-y;
	}

	private static Double multiplication(double x,double y){
		return x*y;
	}
	
	private static Double division(double x,double y){
		return x/y;
	}
}
