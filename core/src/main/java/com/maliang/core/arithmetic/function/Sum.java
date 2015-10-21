package com.maliang.core.arithmetic.function;

import java.util.Collection;
import java.util.Map;

public class Sum {
	private static final int TYPE_STRING = 1;
	private static final int TYPE_INT = 2;
	private static final int TYPE_DOUBLE = 3;
	private static final int TYPE_FLOAT = 4;
	private static final int TYPE_UNKNOW = -1;
	
	public static Object execute(Function function ,Map<String,Object> params){
		Object expressionData = getOperandValue(function,params);
		if(expressionData == null){
			return expressionData;
		}
		
		if(expressionData instanceof Collection){
			expressionData = ((Collection)expressionData).toArray();
		}
		
		Object resultValue = expressionData;
		if(expressionData instanceof Object[]){
			int type = checkDataType(expressionData);
			
			if(TYPE_STRING == type){
				return sumString((Object[])expressionData);
			}else if(TYPE_DOUBLE == type){
				return sumDouble((Object[])expressionData);
			}else if(TYPE_FLOAT == type){
				return sumFloat((Object[])expressionData);
			}else if(TYPE_INT == type){
				return sumInt((Object[])expressionData);
			}
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
	
	private static int checkDataType(Object data){
		boolean isDouble = false;
		boolean isString = false;
		boolean isFloat = false;
		boolean isInt = false;
		
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
		}
		
		return TYPE_UNKNOW;
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
