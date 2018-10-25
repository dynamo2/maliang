package com.maliang.core.util;

import java.util.Map;

import com.maliang.core.service.MapHelper;

public class StringUtil {
	public static boolean isEmpty(String s){
		return s == null || s.trim().isEmpty();
	}
	
	public static String getNotEmptyValue(Map dataMap,String key){
		Object val = MapHelper.readValue(dataMap, key);
		if(val != null && val instanceof String && !StringUtil.isEmpty((String)val)){
			return (String)val;
		}
		
		return null;
	}
	
	public static String toString(Object obj) {
		if(obj == null) {
			return null;
		}
		
		return obj.toString();
	}
}
