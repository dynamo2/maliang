package com.maliang.core.service;

/**
 * Map Reader
 * **/
public class MR {
	public static Object readValue(Object obj,String fname){
		return MapHelper.readValue(obj, fname);
	}
	
	public static void setValue(Object obj,String name,Object keyValue){
		MapHelper.setValue(obj, name, keyValue);
	}
}
