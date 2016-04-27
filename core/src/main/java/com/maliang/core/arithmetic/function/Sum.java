package com.maliang.core.arithmetic.function;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sum {
	private static final int TYPE_STRING = 1;
	private static final int TYPE_INT = 2;
	private static final int TYPE_DOUBLE = 3;
	private static final int TYPE_FLOAT = 4;
	private static final int TYPE_MAP = 5;
	private static final int TYPE_UNKNOW = -1;
	
	public static Object execute(Function function ,Map<String,Object> params){
		Object expressionData = getOperandValue(function,params);
		if(expressionData == null){
			return null;
		}
		
		if(expressionData instanceof Collection){
			expressionData = ((Collection)expressionData).toArray();
		}
		
		Object resultValue = expressionData;
		if(expressionData instanceof Object[]){
			Object[] datas = (Object[])expressionData;
			
			int type = checkDataType(datas);
			if(type == TYPE_MAP){
				datas = sumObject(datas,function,params);
			}
			
			resultValue = doSum(datas);
		}

		return resultValue;
	}
	
	private static Object getOperandValue(Function function,Map<String,Object> params){
		Object value = function.getKeyValue();
		if(!function.useKeyValue()){
			value = function.executeExpression(params);
		}
		return value;
	}

	public static void main(String[] args) {
		boolean i = true;
		boolean ii = false;
		
		if(i){
			System.out.println("is if");
			i = true;
		}else    if(i){
			System.out.println("is else if");
		}
	}
	
	public static Object doSum(Object[] datas){
		int type = checkDataType(datas);
		
		if(TYPE_STRING == type){
			return sumString(datas);
		}else if(TYPE_DOUBLE == type){
			return sumDouble(datas);
		}else if(TYPE_FLOAT == type){
			return sumFloat(datas);
		}else if(TYPE_INT == type){
			return sumInt(datas);
		}
		return null;
	}
	
	private static int checkDataType(Object data){
		boolean isDouble = false;
		boolean isString = false;
		boolean isFloat = false;
		boolean isInt = false;
		boolean isMap = false;
		
		if(data instanceof Object[]){
			for(Object o : (Object[])data){
				if(o == null){
					continue;
				}
				
				if(o instanceof Double){
					isDouble = true;
				}else if(o instanceof Float){
					isFloat = true;
				}else if(o instanceof Integer){
					isInt = true;
				}else if(o instanceof String){
					isString = true;
				}else if(o instanceof Map){
					isMap = true;
				}
			}
		}
		
		if(isString){
			return TYPE_STRING;
		}else if(isDouble){
			return TYPE_DOUBLE;
		}else if(isFloat){
			return TYPE_FLOAT;
		}else if(isInt){
			return TYPE_INT;
		}else if(isMap){
			return TYPE_MAP;
		}
		
		return TYPE_UNKNOW;
	}

	private static Object[] sumObject(Object[] datas,Function fun,Map<String,Object> params){
		List<Object> values = new ArrayList<Object>();
		for(Object obj:datas){
			Map<String,Object> newParams = new HashMap<String,Object>();
			newParams.putAll(params);
			newParams.put("this",obj);
			
			Object val = fun.executeExpression(newParams);
			values.add(val);
		}
		
		return values.toArray();
	}

	private static Double sumDouble(Object[] datas){
		Double value = 0d;
		for(Object d : datas){
			Double dv = toDouble(d);
			if(dv == null)continue;
			
			value += dv;
		}
		return value;
	}
	
	private static Float sumFloat(Object[] datas){
		Float value = 0f;
		for(Object d : datas){
			Float fv = toFloat(d);
			if(fv == null)continue;
			
			value += fv;
		}
		return value;
	}
	
	private static Integer sumInt(Object[] datas){
		Integer value = 0;
		for(Object d : datas){
			Integer iv = toInt(d);
			if(iv == null)continue;
			
			value += iv;
		}
		return value;
	}
	
	private static String sumString(Object[] datas){
		StringBuffer value = null;
		for(Object s : datas){
			if(s == null)continue;
			
			if(value == null){
				value = new StringBuffer(s.toString());
			}else {
				value.append(s);
			}
		}
		return value == null?null:value.toString();
	}
	
	private static Integer toInt(Object obj){
		if(obj == null)return null;
		
		Integer val = null;
		if(val instanceof Number){
			val = ((Number)obj).intValue();
		}else {
			try {
				val = Integer.valueOf(obj.toString());
			}catch(Exception e){
				val = null;
			}
		}
		return val;
	}
	
	private static Float toFloat(Object obj){
		if(obj == null)return null;
		
		Float val = null;
		if(val instanceof Number){
			val = ((Number)obj).floatValue();
		}else {
			try {
				val = Float.valueOf(obj.toString());
			}catch(Exception e){
				val = null;
			}
		}
		return val;
	}
	
	private static Double toDouble(Object d){
		if(d == null)return null;
		
		Double dv = null;
		if(d instanceof Number){
			dv = ((Number)d).doubleValue();
		}else {
			try {
				dv = Double.valueOf(d.toString());
			}catch(Exception e){
				dv = null;
			}
		}
		return dv;
	}
}
