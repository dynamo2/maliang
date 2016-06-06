package com.maliang.core.arithmetic.function;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.maliang.core.util.Utils;

public class FilterFunction {
	public static Object execute(Function function,Map<String,Object> params){
		Object val = function.getKeyValue();
		if(Utils.isArray(val)){
			List newList = new ArrayList();
			for(Object v:Utils.toArray(val)){
				Object matched = function.executeExpression(params);
			}
		}
		return null;
	}
}
