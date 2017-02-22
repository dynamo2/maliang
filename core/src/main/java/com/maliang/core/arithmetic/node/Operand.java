package com.maliang.core.arithmetic.node;

import java.util.Date;
import java.util.Map;

import com.maliang.core.arithmetic.calculator.DateCalculator;

public class Operand extends Node {
	private String operand;
	
	public Operand(String op){
		if(op != null){
			this.operand = op.trim();
		}
	}
	
	public String getOperand(){
		return this.operand;
	}
	
	public Object getValue(Map<String,Object> paramsMap){
		if(this.operand.equalsIgnoreCase("true")){
			return true;
		}
		
		if(this.operand.equalsIgnoreCase("false")){
			return false;
		}
		
		if(this.isString()){
			String s = this.operand.substring(1,this.operand.length()-1);
			return ExpressionNode.doDoubleColon(s, paramsMap);
		}
		
		if(this.operand.startsWith("D")){
			Date date = DateCalculator.readDate(this.operand);
			if(date != null){
				return date;
			}
		}
		
		if(DateCalculator.isDateIncrement(this.operand)){
			return this.operand;
		}
		
		try {
			return new Integer(this.operand);
		}catch(NumberFormatException e){
			try {
				return new Double(this.operand);
			}catch(NumberFormatException e1){}
		}
		
		return new MapNode(this.operand);
		//return MapHelper.readValue(paramsMap,this.operand);
	}
	
	public boolean isString(){
		return this.operand.startsWith("'") && this.operand.endsWith("'");
	}
	
	public String toString(){
		return this.operand;
	}
}
