package com.maliang.core.arithmetic;


public class CompareCalculator {
	public static Boolean calculate(Object valueLeft,Object valueRight,Operator operator){
		if(valueLeft instanceof Comparable){
			try {
				int i = ((Comparable)valueLeft).compareTo(valueRight);
				
				if(operator.isGte()){
					return i >= 0;
				}
				
				if(operator.isLte()){
					return i <= 0;
				}
				
				if(operator.isGt()){
					return i > 0;
				}
				
				if(operator.isEq()){
					return i == 0;
				}
				
				if(operator.isLt()){
					return i < 0;
				}
			}catch(Exception e){
				return false;
			}
		}
		
		return false;
	}
}
