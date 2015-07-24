package com.maliang.core.arithmetic.function;

import java.util.HashMap;
import java.util.Map;

import com.maliang.core.arithmetic.ArithmeticExpression;
import com.maliang.core.service.MapHelper;

public class FunctionBody {
	Object execute(Map<String,Object> params){
		return null;
	}
	
	public static FunctionBody readBody(Function fun){
		if(fun == null || fun.getBody() == null || fun.getBody().isEmpty()){
			return new EmptyBody();
		}
		
		Map<String,Object> map = MapHelper.curlyToMap(fun.getBody());
		if(map.isEmpty()){
			return new ExpressionBody(fun.getBody());
		}else {
			return new MapBody(map);
		}
	}
}

class EmptyBody extends FunctionBody {
	Object execute(Map<String,Object> params){
		return MapHelper.readValue(params, "this");
	}
}

class MapBody extends FunctionBody {
	Map<String,Object> eachMap;
	MapBody(Map<String,Object> map){
		this.eachMap = map;
	}
	
	Object execute(Map<String,Object> params){
		Map<String,Object> newObj = new HashMap<String,Object>();
		
		for(Map.Entry<String, Object> entry : this.eachMap.entrySet()){
			
			System.out.println(entry.getValue());
			String expre = (String)entry.getValue();
			Object value = ArithmeticExpression.execute(expre, params);

			newObj.put(entry.getKey(), value);
		}
		
		return newObj;
	}
	
	public String toString(){
		return this.eachMap.toString();
	}
}

class ExpressionBody extends FunctionBody {
	String expression;
	
	ExpressionBody(String expre){
		this.expression = expre;
	}
	
	Object execute(Map<String,Object> params){
		return ArithmeticExpression.execute(this.expression, params);
	}
	
	public String toString(){
		return this.expression;
	}
}
