package com.maliang.core.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	}
	
	public static Object readValue(Map<String,Object> paramMap,String paramKey,Object defaultValue){
		Object value = readValue(paramMap,paramKey);
		if(value == null){
			return defaultValue;
		}
		
		return value;
	}
	
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
		
		public CurlyCompiler(String source,int s){
			this.cursor = s;
			this.source = source;
			
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
		
		private Map<String,Object> readToMap(){
			Map<String,Object> map = new HashMap<String,Object>();
			String value = null;
			char c = 0;
			this.clearCache();
			for(; cursor < this.source.length();){
				c = readChar();
				
				if(c == '}'){
					if(key != null){
						map.put(key, read(sbf));
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
						value = read(sbf);
						map.put(key, value);
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
				map.put(key, read(sbf));
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
