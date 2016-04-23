package com.maliang.core.arithmetic.function;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.maliang.core.arithmetic.AE;
import com.maliang.core.arithmetic.ArithmeticExpression;

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
		Object o = AE.execute(str);
		System.out.println(o);
	}
	
	private static boolean isArray(Object ob){
		if(ob != null && ((ob instanceof Collection) || (ob instanceof Object[]))){
			return true;
		}
		
		return false;
	}
	
	private static boolean isMap(Object ob){
		if(ob != null && ob instanceof Map){
			return true;
		}
		
		return false;
	}
	
	public static Object execute(Function function,Map<String,Object> params){
		Object operatedObj = function.getKeyValue();
		
		if(isArray(operatedObj)){
			if(operatedObj instanceof Collection){
				operatedObj = ((Collection)operatedObj).toArray();
			}

			List<Object> resultList = new ArrayList<Object>();
			Object[] dataList = (Object[])operatedObj;
			
			for(Object obj : dataList){
				if(!(obj instanceof Map)){
					continue;
				}
				
				Map newParams = new HashMap();
				newParams.putAll((Map)obj);
				newParams.putAll(params);
				
				Object ov = ArithmeticExpression.execute(function.expression, newParams);
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
}
