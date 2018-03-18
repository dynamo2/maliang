package com.maliang.core.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.maliang.core.arithmetic.ArithmeticExpression;
import com.maliang.core.call.CallBack;
import com.maliang.core.util.BeanUtil;
import com.maliang.core.util.StringUtil;
import com.maliang.core.util.Utils;

public class MapHelper {
	public static void main(String[] args) {
		String str = "{_id:objectId,name:Product,"
				+ "fields:[{name:name,type:string,label:名称,unique:{scope:brand}},"
				+ "{name:product_type,type:object,related:ProductType,label:分类,edit:{type:select}},"
				+ "{name:brand,type:object,related:Brand,label:品牌},"
				+ "{name:price,type:double,label:价格},"
				+ "{name:production_date,type:date,label:生产日期},"
				+ "{name:expiry_date,type:date,label:有效期},"
				+ "{name:picture,type:string,label:图片,edit:{type:file}},"
				+ "{name:description,type:string,label:描述,edit:{type:html}}]}";
		//System.out.println(curlyToMap(str));
		//System.out.println(curlyToMap(" { id:  1  ,b:,c:{d:7,e:{g:,f:9}},y:[{aa:4,bb:5,tt:[{aaa:34,bbb:fda,ccc:98}]},{dd:45,ee:fda},{rr:45,hh:fda}] }  "));
		
		str = " { id:  1  ,b:,c:{d:7,e:{g:,f:9}},y:[{aa:4,bb:5,tt:[{aaa:34,bbb:fda,ccc:98}]},{dd:45,ee:fda},{rr:45,hh:fda}] }  ";
		int[] is = matchCoordinate(str,'[',']',0);
		if(is[0] >= 0 && is[1] >= 0){
			String ss = str.substring(is[0],is[1]+1);
			System.out.println("["+is[0]+","+is[1]+"]="+ss);
		}
		
		System.out.println(curlyToMap("{product:this,"
					+ "num:request.product.num,"
					+ "price:user.user_grade.discount*0.01*this.product.price}"));
		
		System.out.println("list class : " + List.class.isAssignableFrom(ArrayList.class));
	}
	
	public static <T> T readValue(Object obj,String fname,Class<T> cls,T defaultValue){
		Object value = readValue(obj,fname);
		if(value == null){
			return defaultValue;
		}
		
		if(cls.isAssignableFrom(value.getClass())){
			return (T)value;
		}
		
		return defaultValue;
	}
	
	public static Object readValue(Object obj,String fname,Object defaultValue){
		Object value = readValue(obj,fname);
		if(value == null){
			return defaultValue;
		}
		
		return value;
	}
	
	/*
	public static Object readValue(Map<String,Object> paramMap,String paramKey,Object defaultValue){
		Object value = readValue(paramMap,paramKey);
		if(value == null){
			return defaultValue;
		}
		
		return value;
	}*/
	
	public static int[] matchCoordinate(String source,char startChar,char endChar,int startIndex){
		int[] coors = new int[]{-1,-1};
		if(source == null || source.trim().isEmpty()){
			return coors;
		}
		
		List<Integer> lefts = new ArrayList<Integer>();
		for(int i = startIndex; i< source.length(); i++){
			char c = source.charAt(i);
			
			if(c == startChar){
				if(coors[0] == -1){
					coors[0] = i;
				}
				lefts.add(i);
				continue;
			}
			
			if(c == endChar){
				if(lefts.size() > 0){
					lefts.remove(lefts.size()-1);
					if(lefts.size() == 0){
						coors[1] = i;
						break;
					}
				}
				continue;
			}
		}
		
		return coors;
	}
	
	public static Object[] readValue(Map<String,Object> paramMap,List<String> keys){
		if(keys == null){
			return null;
		}
		
		Object[] values = new Object[keys.size()];
		int index = 0;
		for(String key:keys){
			values[index++]= readValue(paramMap,key);
		}
		
		return values;
	}
	
	public static Object readValue(Object obj,String fname){
		if(obj == null  || fname == null){
			return null;
		}
		
		return readValue(obj,fname.split("\\."));
	}
	
	public static Object readValue(Object obj,List<String> names){
		if(obj == null  || names == null){
			return null;
		}
		
		return readValue(obj,names.toArray(new String[0]));
	}
	
	public static Object readValue(Object obj,String[] keys){
		if(obj == null  || keys == null){
			return null;
		}
		
		if(keys.length == 0)return obj;
		
		Object value = obj;
		for(String k:keys){
			if(Utils.isArray(value)){
				List tempVal = new ArrayList();

				value = expand(Utils.toArray(value),new String[]{k});
				for(Object item : (Object[])value){
					Object val = doCall(readBeanValue(item,k));
					
					// 待定
					if(val != null){
						tempVal.add(val);
					}
				}
				
				value = null;
				if(tempVal.size() > 0){
					value = tempVal;
				}
			}else {
				value = doCall(readBeanValue(value,k));
			}
		}
		
		return doCall(value);
	}

	private static Object doCall(Object call){
		if(call instanceof CallBack){
			return ((CallBack)call).doCall();
		}
		
		if(call instanceof List && !Utils.isEmpty(call)){
			int i = 0;
			for(Object obj : (List)call){
				((List)call).set(i++,doCall(obj));
			}
		}
		return call;
	}
	
	public static Object[] expand(Object[] expandList,String[] names){
		if(Utils.isEmpty(expandList))return expandList;
		if(Utils.isEmpty(names))return expandList;
		
		int end = 0;
		List<Object> newDatas = new ArrayList<Object>();
		for(Object obj:expandList){
			obj = doCall(obj);
			
			boolean isExpand = false;
			List<String> expands = new ArrayList<String>();
			end = 0;
			Object parent = obj;
			for(String n: names){
				expands.add(n);
				Object val = MapHelper.readValue(parent,n);
				MapHelper.setValue(parent,n,val);

				if(val instanceof List){
					for(Object listObj : (List)val){
						Object nd = Utils.clone(obj);
						
						nd = doCall(nd);
						
						MapHelper.setValue(nd,expands,listObj);
						newDatas.add(nd);
					}

					isExpand = true;
					break;
				}
				
				end++;
				parent = val;
			}

			if(!isExpand){
				newDatas.add(obj);
			}
		}
		
		Object[] results = newDatas.toArray();
		if(end < names.length){
			results = expand(results,names);
		}
		
		return results;
	}
	
	@SuppressWarnings("rawtypes")
	private static Object readBeanValue(Object obj,String fname){
		if(obj == null)return null;
		
		if(obj instanceof Map){
			return ((Map)obj).get(fname);
		}
		return BeanUtil.readValue(obj, fname);
	}
	
	public static void setValue(Object obj,String name,Object keyValue){
		if(StringUtil.isEmpty(name))return;
		
		setValue(obj,name.split("\\."),keyValue);
	}
	
	public static void setValue(Object obj,List<String> names,Object keyValue){
		if(names == null)return;
		
		setValue(obj,names.toArray(new String[0]),keyValue);
	}
	
	public static void setValue(Object obj,String[] keys,Object keyValue){
		if(obj == null  || keys == null){
			return;
		}
		
		if(keys.length == 0)return;
		
		Object value = readValue(obj,Arrays.copyOf(keys,keys.length-1));
		String keyName = keys[keys.length-1];
		if(value instanceof Map){
			((Map)value).put(keyName,keyValue);
		}
	}

	/*
	public static Object readValue(Map<String,Object> paramMap,String paramKey){
		if(paramMap == null || paramMap.size() == 0 
				|| paramKey == null || paramKey.trim().isEmpty()){
			return null;
		}
		
		String[] keys = paramKey.split("\\.");
		int i = 0;
		Map<String,Object> object = paramMap;
		Map<String,Object> parent = paramMap;
		Object value = null;
		for(String k:keys){
			if(i++ < keys.length-1){
				object = (Map<String,Object>)parent.get(k);
				if(object == null){
					return null;
				}
				
				parent = object;
			}else {
				value = object.get(k);
			}
		}
		return value;
	}*/
	
	
	
	public static Map<String,Object> buildAndExecuteMap(String str,Map<String,Object> params){
		if(StringUtil.isEmpty(str)){
			return null;
		}
		
		str = str.trim();
		int start = 0;
		if(str.startsWith("{")){
			start = 1;
		}
		
		return new CurlyCompiler(str,start,params).getMap();
	}
	
	public static Map<String,Object> curlyToMap(String str){
		if(str == null && str.trim().isEmpty()){
			return null;
		}
		
		str = str.trim();
		int start = 0;
		if(str.startsWith("{")){
			start = 1;
		}
		
		return new CurlyCompiler(str,start).getMap();
	}
	
	public static Map<String,Object> curlyToMap(String str,int startIndex){
		if(str == null && str.trim().isEmpty()){
			return null;
		}
		return new CurlyCompiler(str.trim(),startIndex).getMap();
	}
	
	private static class CurlyCompiler {
		private int cursor = 0;
		private String source = null;
		private Map<String,Object> map = null;
		private String key = null;
		private StringBuffer sbf = null;
		private Map<String,Object> params;
		private boolean isExecute = false;
		
		public CurlyCompiler(String source,int s){
			this.cursor = s;
			this.source = source;
			
			this.map = readToMap();
		}
		
		public CurlyCompiler(String source,int s,Map<String,Object> params){
			this.cursor = s;
			this.source = source;
			this.params = params;
			this.isExecute = true;
			
			this.map = readToMap();
		}
		
		public Map<String,Object> getMap(){
			return this.map;
		}
		
		private String read(StringBuffer sbf){
			if(sbf == null){
				return null;
			}
			
			return sbf.toString().trim();
		}
		
		private Object readValue(StringBuffer sbf){
			String expression = this.read(sbf);
			if(this.isExecute){
				Object v = ArithmeticExpression.execute(expression, params);
				
				//System.out.println(expression +"=" + v);
				return v;
			}
			return expression;
		}
		
		private Map<String,Object> readToMap(){
			Map<String,Object> map = new HashMap<String,Object>();
			Object value = null;
			char c = 0;
			this.clearCache();
			for(; cursor < this.source.length();){
				c = readChar();
				
				if(c == '}'){
					if(key != null){
						map.put(key, this.readValue(sbf));
					}
					
					return map;
				}
				if(c == ':'){
					key = read(sbf);
					clearBuffer();
					continue;
				}
				if(c == ','){
					if(key != null){
						map.put(key, this.readValue(sbf));
					}
					
					this.clearCache();
					continue;
				}
				if(c == '{'){
					map.put(key,this.readToMap());
					this.clearCache();
					continue;
				}
				if(c == '['){
					map.put(key,readToList());
					this.clearCache();
					continue;
				}
				
				if(sbf == null){
					sbf = new StringBuffer("");
				}
				sbf.append(c);
			}
			
			if(key != null){
				map.put(key, readValue(sbf));
			}
			return map;
		}
		
		private List<Map<String,Object>> readToList(){
			List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
			for(; cursor < this.source.length();){
				char c = readChar();
				
				if(c == '{'){
					list.add(readToMap());
				}else if(c == ']'){
					break;
				}
			}
			return list;
		}
		
		private void clearBuffer(){
			sbf = null;
		}
		
		private void clearCache(){
			sbf = null;
			key = null;
		}
		private char readChar(){
			return this.source.charAt(cursor++);
		}
	}
}
