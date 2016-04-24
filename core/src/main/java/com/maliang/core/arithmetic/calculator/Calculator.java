package com.maliang.core.arithmetic.calculator;

import com.maliang.core.arithmetic.node.Operator;


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
