package com.maliang.core.arithmetic.function;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.maliang.core.arithmetic.AE;

public class LoopFunction {
	public static void main(String[] args) {
		
	}
	
	public static Object loop(Function function,Map<String,Object> params){
		Object val = function.executeExpression(params);
		if(val == null){
			val = function.getKeyValue();
		}
		
		List<Object> results = new ArrayList<Object>();
		int loop = 0;
		if(val != null && val instanceof Number){
			loop = ((Number)val).intValue();
			
			if(loop < 0){
				loop = 0;
			}
			
			for(int i = 0; i < loop; i++){
				Object newVal = AE.execute(function.getBody(), params);
				results.add(newVal);
			}
		}
		
		return results;
	}
}
