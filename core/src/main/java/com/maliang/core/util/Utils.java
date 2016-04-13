package com.maliang.core.util;

import java.util.Collection;
import java.util.Map;

public class Utils {
	public static boolean isArray(Object ob){
		if(ob != null && ((ob instanceof Collection) || (ob instanceof Object[]))){
			return true;
		}
		
		return false;
	}
	
	public static Object[] toArray(Object ob){
		if(ob != null && ((ob instanceof Collection) || (ob instanceof Object[]))){
			if(ob instanceof Collection){
				return ((Collection)ob).toArray();
			}
			
			if(ob instanceof Object[]){
				return (Object[])ob;
			}
			
			return new Object[]{ob};
		}
		
		return null;
	}
	
	public static boolean isEmpty(Object o){
		if(o == null)return true;
		
		if(o instanceof Object[]){
			return o == null || ((Object[])o).length == 0;
		}
		
		if(o instanceof Collection){
			return o == null || ((Collection)o).size() == 0;
		}
		
		if(o instanceof Map){
			return o == null || ((Map)o).size() == 0;
		}
		
		if(o instanceof String){
			return StringUtil.isEmpty((String)o);
		}
		
		return false;
	}
}
