package com.maliang.core.arithmetic;


public class Calculator {
	public static Object calculate(Object valueLeft,Object valueRight,Operator operator){
		if(operator.isComparison()){
			return CompareCalculator.calculate(valueLeft, valueRight, operator);
		}
		
		if(operator.isLogical()){
			return LogicalCalculator.calculate(valueLeft, valueRight, operator);
		}
		
		return null;
	}
}
