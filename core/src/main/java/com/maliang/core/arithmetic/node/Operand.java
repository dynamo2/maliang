package com.maliang.core.arithmetic.node;

import java.util.Date;
import java.util.Map;

import com.maliang.core.arithmetic.Reader;
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
			String s = null;
			if(this.isTripleQuotes()){
				s = readTripleQuotes();
			}else {
				s = this.operand.substring(1,this.operand.length()-1);
			}
			
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
		return (this.operand.startsWith("'") && this.operand.endsWith("'")) 
				|| (this.operand.startsWith("'''") && this.operand.endsWith("'''"));
	}
	
	public boolean isSingleQuotes(){
		return this.operand.startsWith("'") && this.operand.endsWith("'");
	}
	
	public boolean isTripleQuotes(){
		return this.operand.startsWith("'''") && this.operand.endsWith("'''");
	}
	
	private String readTripleQuotes(){
		int sidx = 3;
		if(sidx >= this.operand.length()){
			return "";
		}
		
		int eidx = this.operand.length()-3;
		if(eidx < sidx){
			eidx = sidx;
		}
		
		return this.operand.substring(sidx,eidx);
	}
	
	public String toString(){
		return this.operand;
	}
}
