package com.maliang.core.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class Utils {
	private final static List<Object> NULL_LIST = new ArrayList<Object>();
	
	static {
		NULL_LIST.add(null);
	}
	
	public static boolean isArray(Object ob){
		return ob != null && ((ob instanceof Collection) || (ob instanceof Object[]));
	}
	
	public static Date parseDate(String format,String dateVal){
		if(!StringUtil.isEmpty(format)){
			try {
				return new SimpleDateFormat(format).parse(dateVal);
			}catch(Exception e){
				return null;
			}
		}
		return parseDate(dateVal);
	}
	
	public static Date parseDate(String dateVal){
		List<String> formats =new ArrayList<String>();
		formats.add("yyyy-MM-dd HH:mm:ss");
		formats.add("yyyy-MM-dd");
		formats.add("yyyy/MM/dd HH:mm:ss");
		formats.add("yyyy/MM/dd");
		formats.add("yyyy年MM月dd日 HH:mm:ss");
		formats.add("yyyy年MM月dd日");
		
		for(String f: formats){
			try {
				return new SimpleDateFormat(f).parse(dateVal);
			}catch(Exception e){}
		}
		return null;
	}
	
	public static Map connect(Map...maps){
		Map newParams = new HashMap();
		for(Map m:maps){
			newParams.putAll(m);
		}
		return newParams;
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
	
	public static List<Object> toList(Object ob){
		Object[] obs = toArray(ob);
		if(isEmpty(obs))return null;
		
		return Arrays.asList(obs);
	}
	
	public static List<Object> clearNull(List<Object> list){
		if(isEmpty(list))return list;
		
		List<Object> nList = new ArrayList<Object>();
		for(Object o : list){
			if(o != null){
				nList.add(o);
			}
		}
		
		return nList;
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
	
	public static HttpSession getSession(){
		try {
			return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getSession();
		}catch(Exception e){
			return null;
		}
	}
	
	public static Object getSessionValue(String key){
		try {
			return getSession().getAttribute(key);
		}catch(Exception e){
			return null;
		}
	}
}
