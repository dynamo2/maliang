package com.maliang.core.arithmetic;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 执行<java::>内的内容，并用执行结果替换表达式
 * ***/
public class DoubleColonReplacer extends Replacer {
	public DoubleColonReplacer(String s){
		super(s,"\\<java::","\\>");
	}
	
	public String doReplace(Object obj){
		Map<String,Object> params = null;
		if(obj instanceof Map){
			params = (Map<String,Object>)obj;
		}
		if(params == null){
			params = new HashMap<String,Object>();
		}
		

		mEnd.find(mStart.start()+7);
		String code = this.source.substring(mStart.start()+7,mEnd.start());
		Object val = AE.execute(code,params);
		if(val == null){
			val = "";
		}
	
		return val.toString();
	}
}