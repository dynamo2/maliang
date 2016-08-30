package com.maliang.core.arithmetic.calculator;

import org.bson.types.ObjectId;

import com.maliang.core.arithmetic.node.Operator;
import com.maliang.core.model.UCType;
import com.maliang.core.model.UCValue;


public class UCTypeCalculator {
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
			
			String r = t.toMaxUnit(v);
			UCValue val = new UCValue();
			val.setType(t);
			val.setValue(r);
			
			return val;
		}

		return null;
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
