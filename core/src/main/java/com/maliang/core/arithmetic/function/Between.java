package com.maliang.core.arithmetic.function;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class Between {
	public static Boolean execute(Function function,Map<String,Object> params){
		Object operatedObj = function.getKeyValue();
		if(operatedObj == null || !(operatedObj instanceof Comparable)){
			return false;
		}
		
		
		
		try {
			Comparable comp = (Comparable)operatedObj;
			Object value = function.executeExpression(params);
			
			if(comp instanceof Date) {
				return DateFunction.between(comp,value);
			}

			if(value instanceof List){
				Comparable min = null;
				Comparable max = null;
				for(Object o : (List)value){
					if(!(o instanceof Comparable)){
						continue;
					}
					
					if(min == null){
						min = (Comparable)o;
					}
					
					if(max == null){
						max = (Comparable)o;
					}
					
					if(min.compareTo(o) > 0){
						min = (Comparable)o;
					}
					
					if(max.compareTo(o) < 0){
						max = (Comparable)o;
					}
				}
				
				if(min == null || max == null){
					return false;
				}
				
				return comp.compareTo(min) >= 0 && comp.compareTo(max) <= 0;
			}else {
				return operatedObj.equals(value);
			}
		}catch(Exception e){
			return false;
		}
	}
}
