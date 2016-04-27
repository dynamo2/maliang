package com.maliang.core.arithmetic.node;

import java.util.Map;

import com.maliang.core.service.MapHelper;

public class MapNode extends Node {
	String key;
	
	public MapNode(String k){
		this.key = k;
	}
	
	public String getExpression(){
		return this.key;
	}
	
	public Object getValue(Object obj){
		return MapHelper.readValue(obj,key);
	}
	
	public Object getValue(Map<String,Object> paramsMap){
		return this.getValue((Object)paramsMap);
	}
}