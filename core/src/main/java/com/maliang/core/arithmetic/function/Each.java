package com.maliang.core.arithmetic.function;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.maliang.core.arithmetic.ArithmeticExpression;
import com.maliang.core.service.DBData;
import com.maliang.core.service.MapHelper;

public class Each {
	public static void main(String[] args) {
		String str = "each(  products  ){product:this,"
				+ "num:request.product.num(index),"
				+ "price:user.user_grade.discount*0.01*this.price}";
		
		List<Map<String,Object>> products = DBData.list("Product");
		Map<String,Object> params = new HashMap<String,Object>();
		Map<String,Object> user = DBData.getRandom("User");
		
		params.put("products", products);
		params.put("user", user);
	}
	
	public static List<Object> execute(Function function,Map<String,Object> params){
		List<Object> resultList = new ArrayList<Object>();
		Object value = function.executeExpression(params);
		if(value == null){
			return resultList;
		}
		if(value instanceof Collection){
			value = ((Collection)value).toArray();
		}
		if(!(value instanceof Object[])){
			throw new RuntimeException("Error parameter to each() function");
		}

		//FunctionBody body = FunctionBody.readBody(function);
		Object[] dataList = (Object[])value;
		int i = 0;
		for(Object data : dataList){
			if(data == null){
				continue;
			}

			Object v = data;
			if(!function.isEmptyBody()){
				params.put("this", data);
				params.put("EACH_CURRENT_INDEX", i++);
				
				v = ArithmeticExpression.execute(function.getBody(), params);
			}
			resultList.add(v);
			
			//resultList.add(body.execute(params));
		}
		
		params.remove("this");
		params.remove("EACH_CURRENT_INDEX");
		return resultList;
	}
	
	/*
	private static EachNode readEachNode(Function fun){
		if(fun == null || fun.getBody() == null || fun.getBody().isEmpty()){
			return new EmptyNode();
		}
		
		Map<String,Object> map = MapHelper.curlyToMap(fun.getBody());
		if(map.isEmpty()){
			return new ExpressionNode(fun.getBody());
		}else {
			return new MapNode(map);
		}
	}
	
	static class EachNode {
		Object execute(Map<String,Object> params){
			return null;
		}
	}
	
	static class EmptyNode extends EachNode {
		Object execute(Map<String,Object> params){
			return params.get("this");
		}
	}
	
	static class MapNode extends EachNode {
		Map<String,Object> eachMap;
		MapNode(Map<String,Object> map){
			this.eachMap = map;
		}
		
		Object execute(Map<String,Object> params){
			Map<String,Object> newObj = new HashMap<String,Object>();
			
			for(Map.Entry<String, Object> entry : this.eachMap.entrySet()){
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
	
	static class ExpressionNode extends EachNode {
		String expression;
		
		ExpressionNode(String expre){
			this.expression = expre;
		}
		
		Object execute(Map<String,Object> params){
			return ArithmeticExpression.execute(this.expression, params);
		}
		
		public String toString(){
			return this.expression;
		}
	}*/
}
