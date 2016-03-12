package com.maliang.core.arithmetic.function;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.maliang.core.arithmetic.ArithmeticExpression;

public class Execute {
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
}
