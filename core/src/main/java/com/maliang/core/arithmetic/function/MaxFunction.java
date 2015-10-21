package com.maliang.core.arithmetic.function;

import java.util.Collection;
import java.util.Map;

import com.maliang.core.arithmetic.ArithmeticExpression;

public class MaxFunction {
	private static boolean isArray(Object ob){
		if(ob != null && ((ob instanceof Collection) || (ob instanceof Object[]))){
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

			Object result = null;
			Object[] dataList = (Object[])operatedObj;
			for(Object obj : dataList){
				if(result == null){
					result = obj;
					continue;
				}
				
				if(obj instanceof Map){
					Comparable lastValue = (Comparable)ArithmeticExpression.execute(function.expression, (Map)result);
					Comparable currValue = (Comparable)ArithmeticExpression.execute(function.expression, (Map)obj);
					if(currValue.compareTo(lastValue) > 0){
						result = obj;
					}
				}else if(obj instanceof Comparable){
					if(((Comparable)obj).compareTo((Comparable)result) > 0){
						result = obj;
					}
				}
			}
			
			return result;
		}

		return null;
	}
}
