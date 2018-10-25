package com.model.controller;

import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;

public class TOStringProcessor implements JsonValueProcessor{

	public Object processArrayValue(Object arg0, JsonConfig arg1) {
		if(arg0 == null)return null;
		
		return arg0.toString();
	}

	public Object processObjectValue(String arg0, Object arg1,
			JsonConfig arg2) {
		if(arg1 == null)return null;
		
		return arg1.toString();
	}
}