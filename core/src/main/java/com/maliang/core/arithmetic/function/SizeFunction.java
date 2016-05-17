package com.maliang.core.arithmetic.function;

import java.util.Map;

import com.maliang.core.util.Utils;

public class SizeFunction {
	public static Integer execute(Function function,Map<String,Object> params){
		Object obj = null;
		if(function.useKeyValue()){
			obj = function.getKeyValue();
		}else {
			obj = (Object)function.executeExpression(params);
		}
		
		if(obj == null)return null;
		
		if(Utils.isArray(obj)){
			return Utils.toArray(obj).length;
		}
		
		if(obj instanceof Map){
			return ((Map)obj).size();
		}
		
		if(obj instanceof String){
			return ((String)obj).length();
		}
		
		return null;
	}
}
