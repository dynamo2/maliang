package com.maliang.core.arithmetic.function;

import java.util.Map;

import org.apache.commons.collections.map.LinkedMap;

import com.maliang.core.arithmetic.AE;
import com.maliang.core.service.MapHelper;

public class AssignFunction {
	public static Object set(Function function,Map<String,Object> params){
		String key = function.getKeySource();
		if(key.contains(".")){
			Object newVal = function.executeExpression(params);
			
			String[] names = key.split("\\.");
			Map<String,Object> parent = params;
			for(int i = 0; i < names.length-2; i++){
				Object v = MapHelper.readValue(parent,names[i]);
				if(v == null || !(v instanceof Map)){
					v = new LinkedMap();
					
					parent.put(names[i],v);
				}
				
				parent = (Map<String,Object>)v;
			}
			
			parent.put(names[names.length-2],newVal);
			return newVal;
		}
		
		return null;
	}
	
	public static Object append(Function function,Map<String,Object> params){
		String key = function.getKeySource();
		if(key.contains(".")){
			Object newVal = function.executeExpression(params);
			
			String[] names = key.split("\\.");
			Map<String,Object> parent = params;
			for(int i = 0; i < names.length-2; i++){
				Object v = MapHelper.readValue(parent,names[i]);
				if(v == null || !(v instanceof Map)){
					v = new LinkedMap();
					
					parent.put(names[i],v);
				}
				
				parent = (Map<String,Object>)v;
			}
			
			parent.put(names[names.length-2],newVal);
			return newVal;
		}
		
		return null;
	}
	
	public static void main(String[] args) {
		String s = "{aaa:'aaa',c:b.eee.fff.cc.ccc.ggg.bb.bbb.set({cc:{ccc:{ggg:[999,33,22,77]}}}),d:b.eee.fff.cc.ccc.ggg.bb.bbb.cc.ccc.ggg.size}";
		Object v = AE.execute(s);
		
		System.out.println(v);
	}
	
	public static Object execute(Function function,Map<String,Object> params){
		Object operatedObj = function.getKeyValue();
		if(operatedObj == null || !(operatedObj instanceof Comparable)){
			return false;
		}
		return null;
	}
}
