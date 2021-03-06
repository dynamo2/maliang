package com.maliang.core.arithmetic.function;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.maliang.core.arithmetic.AE;
import com.maliang.core.arithmetic.ArithmeticExpression;
import com.maliang.core.util.Utils;

public class QueryFunction {
	public static void main(String[] args) {
		String str = "[{name:'wmx',age:20,grade:{id:2,name:'普通会员'}},"
				+ "{name:'wf',age:30,grade:{id:2,name:'普通会员'}},"
				+ "{name:'wzq',age:4,grade:{id:4,name:'高级会员'}},"
				+ "{name:'wzq',age:5,grade:{id:8,name:'高级会员'}}].query(((age>10 & name='wmx') | grade.id=8 | (age=grade.id))).max(grade.id)";
		
//		Object ov = ArithmeticExpression.execute(str,null);
//		System.out.println(ov);
//		
//		str = "[1,2,3,4,5].max()";
//		ov = ArithmeticExpression.execute(str,null);
//		System.out.println("ov : " + ov);
//		
		str = "db.Product.search()";
		
		str = "{users:{name:'wzq',age:5,grade:{id:8,name:'高级会员'}},rn:'name'}";
		Map ps = (Map)AE.execute(str);
		
		str = "users.remove(rn)";
		Object o = AE.execute(str,ps);
		System.out.println(o);
	}
	
	private static boolean isMap(Object ob){
		if(ob != null && ob instanceof Map){
			return true;
		}
		
		return false;
	}

	public static Object findOne(Function function,Map<String,Object> params){
		Object result = find(function,params);
		if(Utils.isArray(result)){
			if(Utils.isEmpty(result)){
				return null;
			}
			
			return Utils.toArray(result)[0];
		}
		return result;
	}
	
	public static Object find(Function function,Map<String,Object> params){
		Object operatedObj = function.getKeyValue();
		
		if(Utils.isEmpty(function.expression)) {
			return operatedObj;
		}
		
		if(Utils.isArray(operatedObj)){
			List<Object> resultList = new ArrayList<Object>();
			
			for(Object obj : Utils.toList(operatedObj)){
				if(!(obj instanceof Map)){
					continue;
				}
				
				Map newParams = Utils.connect(params,(Map)obj);
				Object ov = AE.execute(function.expression, newParams);
				if(ov instanceof Boolean && (Boolean)ov){
					resultList.add(obj);
				}
			}
			
			return resultList;
		}
		
		if(isMap(operatedObj)){
			Object ov = ArithmeticExpression.execute(function.expression, (Map)operatedObj);
			if(ov instanceof Boolean && (Boolean)ov){
				return operatedObj;
			}
			return null;
		}

		return null;
	}
	
	public static Object remove(Function function,Map<String,Object> params){
		Object operatedObj = function.getKeyValue();
		if(Utils.isArray(operatedObj)){
			
			List<Object> resultList = new ArrayList<Object>();
			for(Object obj : Utils.toArray(operatedObj)){
				Map newParams = Utils.connect(params);
				if(obj instanceof Map){
					newParams = Utils.connect(newParams,(Map)obj);
				}
				
				Object ov = ArithmeticExpression.execute(function.expression, newParams);
				if((ov == null && obj == null) || (ov instanceof Boolean && (Boolean)ov)){
					continue;
				}
				resultList.add(obj);
			}
			
			return resultList;
		}
		
		if(isMap(operatedObj)){
			Object ov = ArithmeticExpression.execute(function.expression, Utils.connect(params,(Map)operatedObj));
			((Map)operatedObj).remove(ov);
			return operatedObj;
		}

		return null;
	}
	
	public static Object removeKey(Function function,Map<String,Object> params){
		Object operatedObj = function.getKeyValue();
		Object ov = AE.execute(function.expression, params);
		
		if(Utils.isIntegers(ov)) {
			if(Utils.isArray(operatedObj)) {
				List list = Utils.toList(operatedObj);
				List result = new ArrayList();
				List rks = Utils.toList(ov);
				for(int i = 0; i < list.size(); i++) {
					if(!rks.contains(i)) {
						result.add(list.get(i));
					}
				}
				
				return result;
			}
			
			return operatedObj;
		}
		
		for(Object obj : Utils.toList(operatedObj,true)) {
			if(obj instanceof Map) {
				doRemoveKey((Map)obj,ov);
			}
		}

		return operatedObj;
	}
	
	private static void doRemoveKey(Map map,Object ks) {
		for(Object k : Utils.toList(ks,true)) {
			map.remove(k);
		}
	}
}
