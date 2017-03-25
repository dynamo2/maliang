package com.maliang.core.arithmetic.function.aggregation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.maliang.core.arithmetic.function.Function;

public class Sum {
	private static final int TYPE_STRING = 1;
	private static final int TYPE_INT = 2;
	private static final int TYPE_DOUBLE = 3;
	private static final int TYPE_FLOAT = 4;
	private static final int TYPE_MAP = 5;
	private static final int TYPE_UNKNOW = -1;
	
	static Object doSum(Object[] datas){
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
	
	private static Object getOperandValue(Function function,Map<String,Object> params){
		Object value = function.getKeyValue();
		if(!function.useKeyValue()){
			value = function.executeExpression(params);
		}
		return value;
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
					isDouble = true;
				}else if(o instanceof Integer){
					isDouble = true;
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
	
	private static boolean canSum(Object o){
		return (o instanceof Double || o instanceof Float || o instanceof Integer || o instanceof String);
	}
	
	private static Object[] sumObject(Object[] datas,Function fun,Map<String,Object> params){
		List<Object> values = new ArrayList<Object>();
		for(Object obj:datas){
			if(canSum(obj)){
				values.add(obj);
				continue;
			}
			
			Map<String,Object> newParams = new HashMap<String,Object>();
			newParams.putAll(params);
			newParams.put("this",obj);
			
			Object val = fun.executeExpression(newParams);
			values.add(val);
		}
		
		return values.toArray();
	}
	
	private static Double sumDouble(Object[] datas){
		Double value = null;
		for(Object d : datas){
			if(d == null)continue;
			
			if(value == null){
				value = (Double)d;
			}else value += (Double)d;
		}
		return value;
	}
	
	private static Float sumFloat(Object[] datas){
		Float value = null;
		for(Object d : datas){
			if(d == null)continue;
			
			if(value == null){
				value = (Float)d;
			}else value += (Float)d;
		}
		return value;
	}
	
	private static Integer sumInt(Object[] datas){
		Integer value = null;
		for(Object d : datas){
			if(d == null)continue;
			
			if(value == null){
				value = (Integer)d;
			}else value += (Integer)d;
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
}

