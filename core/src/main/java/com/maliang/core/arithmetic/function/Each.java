package com.maliang.core.arithmetic.function;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.maliang.core.arithmetic.AE;
import com.maliang.core.arithmetic.ArithmeticExpression;
import com.maliang.core.exception.Break;
import com.maliang.core.util.Utils;

public class Each {
	public static void main(String[] args) {
		String str = "{p:{name:'鱼子酱眼霜',price:345,num:54},ps:[{name:'全能乳液',price:800,num:3},{name:'防晒霜',price:321,num:2}],"
						+ "c:each(ps){{p:this.aaa.set('aaa'),p:this,totalPrice:p.price*p.num,name:p.name}},"
						+ "c:p.name,ccc:ps}";

		Object val = AE.execute(str);
		System.out.println(val);
	}
	
	public static List<Object> eachUpdate(Function function,Map<String,Object> params){
		if(params == null)params = new HashMap<String,Object>();
		
		List<Object> resultList = new ArrayList<Object>();
		
		Object value = null;
		if(function.useKeyValue()){
			value = function.getKeyValue();
		}else {
			value = (Object)function.executeExpression(params);
		}
		
		if(value == null){
			return resultList;
		}
		
		if(Utils.isArray(value)){
			value = Utils.toArray(value);
		}
		if(!(value instanceof Object[])){
			throw new RuntimeException("Error parameter to eachUpdate() function");
		}
		
		Map<String,Object> eachParams = new HashMap<String,Object>();
		eachParams.putAll(params);
		Function.pushThis(eachParams);
		
		int i = 0;
		boolean isBreak = false;
		for(Object data : (Object[])value){
			if(data == null){
				continue;
			}

			Object oldVal = data;
			if(!function.isEmptyBody()){
				eachParams.put("this", data);
				eachParams.put("EACH_CURRENT_INDEX", i);

				try {
					Object newVal = AE.execute(function.getBody(), eachParams);
					if(oldVal == null || !(oldVal instanceof Map) 
							|| !(newVal instanceof Map)){
						oldVal = newVal;
					}else {
						AssignFunction.merge((Map<String,Object>)oldVal, (Map<String,Object>)newVal);
					}
				}catch(Break be){
					isBreak = true;
				}
			}
			((Object[])value)[i] = oldVal;
			
			i++;
			if(isBreak)break;
		}
		
		return Utils.toList(value);
	}
	
	public static List<Object> execute(Function function,Map<String,Object> params){
		if(params == null)params = new HashMap<String,Object>();
		
		List<Object> resultList = new ArrayList<Object>();
		Object value = null;
		if(function.useKeyValue()){
			value = function.getKeyValue();
		}else {
			value = (Object)function.executeExpression(params);
		}
		
		if(value == null){
			return resultList;
		}
		
		if(value instanceof Integer && ((Integer)value) > 0) {
			Integer[] tempVal = new Integer[((Integer)value)];
			for(int i = 0; i < ((Integer)value); i++) {
				tempVal[i] = i;
			}
			value = tempVal;
		}

		value = Utils.toArray(value);
		if(!(value instanceof Object[])){
			throw new RuntimeException("Error parameter to each() function");
		}
		
		Map<String,Object> eachParams = new HashMap<String,Object>();
		eachParams.putAll(params);
		AssignFunction.pushWholeVariable(eachParams, params);

		Function.pushThis(eachParams);
		
		Object[] dataList = (Object[])value;
		int i = 0;
		boolean isBreak = false;
		for(Object data : dataList){
			if(data == null){
				continue;
			}

			Object v = data;
			if(!function.isEmptyBody()){
				eachParams.put("this", data);
				eachParams.put("EACH_CURRENT_INDEX", i++);

				try {
					v = AE.execute(function.getBody(), eachParams);
				}catch(Break be){
					isBreak = true;
				}
			}
			resultList.add(v);
			
			if(isBreak)break;
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
