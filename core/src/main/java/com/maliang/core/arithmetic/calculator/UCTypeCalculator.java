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
			UCType t = x.getType();
			int v1 = t.toMinUnit(x.getValue());
			int v2 = t.toMinUnit(y.getValue());
			
			Integer v = null;
			if(operator.isPlus()){
				v = plus(v1,v2);
			}else if(operator.isSubstruction()){
				v = substruction(v1,v2);
			}
			
			return new UCValue(t.toMaxUnit(v),t);
		}

		return null;
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
	
	private static int substruction(int x,int y){
		return x-y;
	}
	
	private static boolean isSameUCType(UCType t1,UCType t2){
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
