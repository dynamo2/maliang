package com.maliang.core.arithmetic.node;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.maliang.core.arithmetic.calculator.CompareCalculator;
import com.maliang.core.arithmetic.calculator.DateCalculator;
import com.maliang.core.arithmetic.calculator.DoubleCalculator;
import com.maliang.core.arithmetic.calculator.IntegerCalculator;
import com.maliang.core.arithmetic.calculator.LogicalCalculator;

public class ExpressionNode extends Node {
	private Node left;
	private Node right;
	private Operator operator;
	
	public Node getLeft() {
		return left;
	}
	public void setLeft(Node left) {
		this.left = left;
	}
	public Node getRight() {
		return right;
	}
	public void setRight(Node right) {
		this.right = right;
	}
	public Operator getOperator() {
		return operator;
	}
	public void setOperator(Operator operator) {
		this.operator = operator;
	}
	
	public Object getValue(Map<String,Object> paramsMap){
		if(this.right == null){
			return singleExpression(paramsMap);
		}

		if(this.operator.isDot()){
			return calculateDot(paramsMap);
		}
		
		Object valueLeft = this.left.getValue(paramsMap);
		Object valueRight = this.right.getValue(paramsMap);
		if(valueLeft instanceof MapNode){
			valueLeft = ((MapNode)valueLeft).getValue(paramsMap);
		}
		if(valueRight instanceof MapNode){
			valueRight = ((MapNode)valueRight).getValue(paramsMap);
		}

		if(this.operator.isLogical()){
			return LogicalCalculator.calculate(valueLeft, valueRight, operator);
		}
		
		if(this.operator.isComparison()){
			return CompareCalculator.calculate(valueLeft, valueRight, operator);
		}
		
		if(valueLeft instanceof Date){
			return DateCalculator.calculate((Date)valueLeft,valueRight.toString(),this.operator);
		}
		
		if(valueRight instanceof Date){
			return DateCalculator.calculate((Date)valueRight,valueLeft.toString(),this.operator);
		}
		
		if(valueLeft instanceof List){
			List list = (List)valueLeft;
			if(valueRight == null){
				return list;
			}else if(valueRight instanceof List){
				if(!this.operator.isPlus()){
					throw new RuntimeException("Error operator '"+this.operator.getOperatorKey()+"' for String");
				}
				
				list.addAll((List)valueRight);
				return list;
			}else {
				return calculateString(valueLeft.toString(),valueRight.toString());
			}
		}
		
		if(valueLeft instanceof Map){
			Map map = (Map)valueLeft;
			if(valueRight == null){
				return map;
			}else if(valueRight instanceof Map){
				if(!this.operator.isPlus()){
					throw new RuntimeException("Error operator '"+this.operator.getOperatorKey()+"' for String");
				}
				
				map.putAll((Map)valueRight);
				return map;
			}else {
				return calculateString(valueLeft.toString(),valueRight.toString());
			}
		}
		
		if(valueRight instanceof List){
			if(valueLeft == null){
				return (List)valueRight;
			}
		}
		
		if(valueRight instanceof Map){
			if(valueLeft == null){
				return (Map)valueRight;
			}
		}

		if(valueLeft instanceof String || valueRight instanceof String){
			return calculateString(valueLeft==null?null:valueLeft.toString(),valueRight==null?null:valueRight.toString());
		}

		if(valueLeft instanceof Integer && valueRight instanceof Integer){
			return IntegerCalculator.calculate((Integer)valueLeft,(Integer)valueRight,this.operator);
		}
		
		//待定
		if(valueLeft == null || valueRight == null){
			return null;
		}

		double dl = ((Number)valueLeft).doubleValue();
		double dr = ((Number)valueRight).doubleValue();
		return DoubleCalculator.calculate(dl,dr,this.operator);
	}
	
	/**
	 * 单一操作数的计算
	 * **/
	private Object singleExpression(Map<String,Object> paramsMap){
		Object leftV = this.left.getValue(paramsMap);
		if(this.operator.isAnd()){
			return false;
		}else if(this.operator.isOr()){
			return LogicalCalculator.getBoolean(leftV);
		}
		
		return leftV;
	}
	
	/**
	 * '.'操作符的运算
	 * **/
	private Object calculateDot(Map<String,Object> paramsMap){
		Object valueLeft = this.left.getValue(paramsMap);
		Object valueRight = null;
		
		if(valueLeft instanceof MapNode){
			valueLeft = ((MapNode)valueLeft).getValue(paramsMap);
		}
		
		if(this.right instanceof Operand){
			valueRight = this.right.getValue(paramsMap);
			
			if(valueRight instanceof MapNode){
				return ((MapNode)valueRight).getValue(valueLeft);
			}
			
			return null;
		}
		if(this.right instanceof FunctionNode){
			((FunctionNode)this.right).getFunction().setKeyValue(valueLeft);
			return this.right.getValue(paramsMap);
		}
		
		return null;
	}
	
	
	private String calculateString(String left,String right){
		if(!this.operator.isPlus()){
			throw new RuntimeException("Error operator '"+this.operator.getOperatorKey()+"' for String");
		}
		
		return left+right;
	}
	
	public String toString(){
		return "("+this.left+this.operator+this.right+")";
		//return ""+this.left+this.operator+this.right;
	}
}
