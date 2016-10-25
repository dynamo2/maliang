package com.maliang.core.arithmetic.calculator;

import org.bson.types.ObjectId;

import com.maliang.core.arithmetic.exception.Continue;
import com.maliang.core.arithmetic.node.Operator;
import com.maliang.core.model.UCType;
import com.maliang.core.model.UCValue;


public class UCTypeCalculator {
	public static UCValue calculate(Object o1,Object o2,Operator operator){
		UCValue x = null;
		UCValue y = null;
		if(o1 instanceof UCValue){
			x = (UCValue)o1;
		}
		if(o2 instanceof UCValue){
			y = (UCValue)o2;
		}
		
		if(x == null && y == null)return null;
		
		if(x == null){
			x = toUCValue(o1,y.getType());
		}
		
		if(y == null){
			y = toUCValue(o2,x.getType());
		}
		
		return calculate(x,y,operator);
	}

	public static UCValue calculate(UCValue x,UCValue y,Operator operator){
		if(isSameUCType(x.getType(),y.getType())){
			if(operator.isPlus()){
				return doSum(new UCValue[]{x,y});
			}else if(operator.isSubstruction()){
				return substruction(x,y);
			}
			
			return null;
		}

		return null;
	}
	
	public static UCValue doSum(UCValue[] vals){
		int sum = 0;
		UCType t = null;
		for(UCValue v : vals){
			if(t == null)t = v.getType();
			sum += v.toInt();
		}
		
		return new UCValue(sum,t);
	}
	
	private static UCValue toUCValue(Object o,UCType type){
		Object v = UCValue.parse(o,type);
		if(v instanceof UCValue){
			return (UCValue)v;
		}else {
			throw new Continue("Continue");
		}
	}
	
	private static int plus(int x,int y){
		return x+y;
	}
	
	private static UCValue substruction(UCValue x,UCValue y){
		return new UCValue(x.toInt()-y.toInt(),x.getType());
	}
	
	public static boolean isSameUCType(UCType t1,UCType t2){
		if(t1 == null || t2 == null || t1.getId() == null || t2.getId() == null){
			return false;
		}
		
		return t1.getId().equals(t2.getId());
	}
	
	public static void main(String[] args) {
		ObjectId id = new ObjectId("57bbcf078f77dd8bb660b3e6");
		ObjectId i2 = new ObjectId("57bbcf078f77dd8bb660b3e6");
		
		System.out.println(id.equals(i2));
	}
	
	private static int multiplication(int x,int y){
		return x*y;
	}
	
	private static int division(int x,int y){
		return x/y;
	}
}
