package com.maliang.core.arithmetic.function;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.maliang.core.arithmetic.AE;
import com.maliang.core.arithmetic.ArithmeticExpression;

public class Each {
	public static void main(String[] args) {
		String str = "{p:{name:'鱼子酱眼霜',price:345,num:54},ps:[{name:'全能乳液',price:800,num:3},{name:'防晒霜',price:321,num:2}],"
						+ "c:each(ps){{p:this.aaa.set('aaa'),p:this,totalPrice:p.price*p.num,name:p.name}},"
						+ "c:p.name,ccc:ps}";

		Object val = AE.execute(str);
		System.out.println(val);
	}
	
	public static List<Object> execute(Function function,Map<String,Object> params){
		if(params == null)params = new HashMap<String,Object>();
		
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
		
		Map<String,Object> eachParams = new HashMap<String,Object>();
		eachParams.putAll(params);
		
		//eachParams = params;

		Function.pushThis(eachParams);
		//FunctionBody body = FunctionBody.readBody(function);
		Object[] dataList = (Object[])value;
		int i = 0;
		for(Object data : dataList){
			if(data == null){
				continue;
			}

			Object v = data;
			if(!function.isEmptyBody()){
				eachParams.put("this", data);
				eachParams.put("EACH_CURRENT_INDEX", i++);

				v = ArithmeticExpression.execute(function.getBody(), eachParams);
			}
			resultList.add(v);
		}
		
		eachParams.remove("this");
		eachParams.remove("EACH_CURRENT_INDEX");
		
		Function.popThis(params);
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
