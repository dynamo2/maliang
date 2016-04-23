package com.maliang.core.arithmetic.function;

import java.util.Map;

import com.maliang.core.arithmetic.AE;
import com.maliang.core.arithmetic.ArithmeticExpression;
import com.maliang.core.util.Utils;

public class AggregationFunction {
	public static Object max(Function function,Map<String,Object> params){
		return doCompare(function,params,"max");
	}
	
	public static Object min(Function function,Map<String,Object> params){
		return doCompare(function,params,"min");
	}

	private static Object doCompare(Function function,Map<String,Object> params,String match){
		Object operatedObj = readOperand(function,params);
		Object[] dataList = Utils.toArray(operatedObj);
		if(!Utils.isEmpty(dataList)){
			
			Object result = null;
			for(Object obj : dataList){
				if(result == null){
					result = obj;
					continue;
				}
				
				if(obj instanceof Map){
					Comparable lastValue = (Comparable)ArithmeticExpression.execute(function.expression, (Map)result);
					Comparable currValue = (Comparable)ArithmeticExpression.execute(function.expression, (Map)obj);
					if(match(currValue,lastValue,match)){
						result = obj;
					}
//					if(currValue.compareTo(lastValue) > 0){
//						result = obj;
//					}
				}else if(obj instanceof Comparable){
					if(match((Comparable)obj,(Comparable)result,match)){
						result = obj;
					}
//					if(((Comparable)obj).compareTo((Comparable)result) > 0){
//						result = obj;
//					}
				}
			}
			
			return result;
		}

		return null;
	}
	
	private static boolean match(Comparable c1,Comparable c2,String match){
		int result = compare(c1,c2);
		if("max".equals(match)){
			return result > 0;
		}
		
		if("min".equals(match)){
			return result < 0;
		}
		
		if("eq".equals(match)){
			return result == 0;
		}
		
		return false;
	}
	
	private static int compare(Comparable c1,Comparable c2){
		if(c1 instanceof String || c2 instanceof String){
			return c1.toString().compareTo(c2.toString());
		}
		
		if(c1 instanceof Number && c2 instanceof Number){
			return new Double(((Number)c1).doubleValue()).compareTo(new Double(((Number)c2).doubleValue()));
		}
		
		return -1;
	}
	
	private static Object readOperand(Function fun,Map<String,Object> params){
		if(fun.useKeyValue()){
			return fun.getKeyValue();
		}else {
			return fun.executeExpression(params);
		}
	}
	
	public static void main(String[] args) {
		String s = "max([1.00,2.99,3,5,6])";
		Object v = AE.execute(s);
		System.out.println(v);
	}
}
