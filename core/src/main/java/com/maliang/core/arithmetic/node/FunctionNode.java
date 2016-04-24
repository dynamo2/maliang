package com.maliang.core.arithmetic.node;

import java.util.Map;

import com.maliang.core.arithmetic.function.Function;

public class FunctionNode extends Node {
	private Function function;
	
	public FunctionNode(Function fun){
		this.function = fun;
	}
	
	public Function getFunction(){
		return this.function;
	}
	
	public Object getValue(Map<String,Object> paramsMap){
		return this.function.execute(paramsMap);
	}
}
