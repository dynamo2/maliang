package com.maliang.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utils {
	public static boolean isArray(Object ob){
		return ob != null && ((ob instanceof Collection) || (ob instanceof Object[]));
	}
	
	public static Map<Object,Object> clone(Map<Object,Object> map){
		if(map == null)return null;
		if(map.size() == 0)return new HashMap<Object,Object>();
		
		Map<Object,Object> newMap = new HashMap<Object,Object>();
		for(Object k : map.keySet()){
			Object val = clone(map.get(k));
			newMap.put(k,val);
		}
		return newMap;
	}
	
	public static List<Object> clone(List<Object> list){
		if(list == null)return null;
		if(list.size() == 0)return new ArrayList<Object>();
		
		List<Object> newList = new ArrayList<Object>();
		for(Object v:newList){
			Object nv = clone(v);
			newList.add(nv);
		}
		return newList;
	}
	
	public static Object clone(Object obj){
		if(obj == null)return null;
		if(obj instanceof Map){
			return clone((Map)obj);
		}
		if(obj instanceof List){
			return clone((List)obj);
		}
		return obj;
	}
	
	public static Object[] toArray(Object ob){
		if(isArray(ob)){
			if(ob instanceof Collection){
				return ((Collection)ob).toArray();
			}
			
			if(ob instanceof Object[]){
				return (Object[])ob;
			}
		}
		
		if(ob != null){
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
