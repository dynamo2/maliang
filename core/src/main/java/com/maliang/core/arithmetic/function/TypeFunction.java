package com.maliang.core.arithmetic.function;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.maliang.core.arithmetic.AE;
import com.maliang.core.util.Utils;

public class TypeFunction {
	private static final int TYPE_INT = 1;
	private static final int TYPE_DOUBLE = 2;
	private static final int TYPE_FLOAT = 3;
	private static final int TYPE_BYTE = 4;
	private static final int TYPE_LONG = 5;
	private static final int TYPE_SHORT = 6;
	private static final int TYPE_STRING = 7;
	
	public static Object intExecute(Function function,Map<String,Object> params){
		return doNumber(getOperandValue(function,params),TYPE_INT);
	}

	public static Object doubleExecute(Function function,Map<String,Object> params){
		return doNumber(getOperandValue(function,params),TYPE_DOUBLE);
	}

	public static Object floatExecute(Function function,Map<String,Object> params){
		return doNumber(getOperandValue(function,params),TYPE_FLOAT);
	}
	
	public static Object longExecute(Function function,Map<String,Object> params){
		return doNumber(getOperandValue(function,params),TYPE_LONG);
	}
	
	public static Object shortExecute(Function function,Map<String,Object> params){
		return doNumber(getOperandValue(function,params),TYPE_SHORT);
	}

	public static Object stringExecute(Function function,Map<String,Object> params){
		return doString(getOperandValue(function,params));
	}
	
	public static void main(String[] args) {
		String s = "{types:['1','2','3',['5','6','7',[8,9,'10']],11,12]}";
		Map<String,Object> p = (Map<String,Object>)AE.execute(s);
		
		s = "types.long()";
		Object v = AE.execute(s,p);
		System.out.println(v);
		System.out.println(Utils.toArray(v)[0].getClass());
	}
	
	private static Object getOperandValue(Function function,Map<String,Object> params){
		Object value = function.getKeyValue();
		if(!function.useKeyValue()){
			value = function.executeExpression(params);
		}
		return value;
	}
	
	private static List<Object> doList(Object[] vals,int type){
		if(Utils.isEmpty(vals))return null;
		
		List<Object> list = new ArrayList<Object>();
		for(Object o : vals){
			if(type == TYPE_STRING){
				o = doString(o);
			}else {
				o = doNumber(o,type);
			}

			if(o != null){
				list.add(o);
			}
		}
		return list;
	}
	
	private static Object doNumber(Object value,int type){
		if(Utils.isArray(value)){
			return doList(Utils.toArray(value),type);
		}

		Number nv = null;
		if(value instanceof Number){
			nv = (Number)value;
		}
		
		try {
			if(nv == null){
				nv = new Double(value.toString());
			}
		}catch(Exception e){}
		
		if(nv == null)return null;
		
		if(type == TYPE_INT){
			return nv.intValue();
		}else if(type == TYPE_FLOAT){
			return nv.floatValue();
		}else if(type == TYPE_DOUBLE){
			return nv.doubleValue();
		}else if(type == TYPE_BYTE){
			return nv.byteValue();
		}else if(type == TYPE_LONG){
			return nv.longValue();
		}else if(type == TYPE_SHORT){
			return nv.shortValue();
		}
		
		return null;
	}
	
	private static Object doString(Object value){
		if(Utils.isArray(value)){
			return doList(Utils.toArray(value),TYPE_STRING);
		}
		
		if(value instanceof String){
			return (String)value;
		}
		
		try {
			return value.toString();
		}catch(Exception e){
			return null;
		}
	}
	
//	private static Object floatValueOf(Object value){
//		if(Utils.isArray(value)){
//			return doList(Utils.toArray(value),TYPE_FLOAT);
//		}
//		
//		if(value instanceof Number){
//			return ((Number)value).floatValue();
//		}
//		
//		try {
//			return new Float(value.toString());
//		}catch(Exception e){
//			try {
//				return new Double(value.toString()).floatValue();
//			}catch(Exception ee){
//				return null;
//			}
//		}
//	}
	
	
}
