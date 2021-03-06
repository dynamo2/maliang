package com.maliang.core.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
	
	public static void main(String[] args) {
		Date d = Utils.parseDate("14:00");
		System.out.println("d : " + d);
	}
	
	public static Date parseDate(Object dateVal){
		if(dateVal == null) {
			return null;
		}
		
		if(dateVal instanceof Date) {
			return (Date)dateVal;
		}
		
		return parseDate(dateVal.toString());
	}
	
	public static Date parseDate(String dateVal){
		List<String> formats =new ArrayList<String>();
//		formats.add("yyyyMMdd HH:mm:ss");
//		formats.add("yyyyMMddHHmmss");
//		formats.add("yyyyMMdd");
		formats.add("yyyy-MM-dd HH:mm:ss");
		formats.add("yyyy-MM-dd");
		formats.add("yyyy/MM/dd HH:mm:ss");
		formats.add("yyyy/MM/dd");
		formats.add("yyyy年M月d日 HH:mm:ss");
		formats.add("yyyy年M月d日");
		formats.add("HH:mm");
		
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
			newMap.put(k,clone(map.get(k)));
		}
		return newMap;
	}
	
	public static List<Object> clone(List<Object> list){
		if(list == null)return null;
		if(list.size() == 0)return new ArrayList<Object>();
		
		List<Object> newList = new ArrayList<Object>();
		for(Object v:list){
			newList.add(clone(v));
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
		return toList(ob,false);
	}
	
	
	/***
	 * 将数据转换成List类型
	 * 1. 如果数据已经是List类型，直接类型转换并返回
	 * 2. 如果数据不是List类型，新建List，将数据插入List，并返回List
	 * 3. 如果数据为Null，根据returnEmpty返回：
	 * 	  returnEmpty == true ：返回空的List
	 *    returnEmpty == false：返回Null
	 * **/
	public static List<Object> toList(Object ob,boolean returnEmpty){
		if(ob == null){
			if(returnEmpty){
				return new ArrayList<Object>();
			}
			
			return null;
		}
		
		if(ob instanceof List){
			return (List<Object>)ob;
		}
		
		if(ob instanceof Object[]) {
			List list = new ArrayList();
			for(Object ov:(Object[])ob) {
				list.add(ov);
			}
			return list;
		}
		
		List<Object> list = new ArrayList<Object>();
		list.add(ob);
		
		return list;
	}
	
	public static boolean toBoolean(Object val) {
		if(val == null) {
			return false;
		}
		
		if(val instanceof Boolean) {
			return (Boolean)val;
		}
		
		if("true".equalsIgnoreCase(val.toString())) {
			return true;
		}
		return false;
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
	
	public static boolean isIntegers(Object obj) {
		if(Utils.isEmpty(obj)) {
			return false;
		}
		
		boolean is = false;
		for(Object o : toList(obj)) {
			is = isInteger(o);
			
			if(!is) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean isInteger(Object obj) {
		if(obj == null)return false;
		
		return obj instanceof Integer;
	}
	
	public static boolean isNumbric(Object obj) {
		boolean is = false;
		for(Object o : toList(obj)) {
			if(o instanceof Number) {
				is = true;
			}else {
				is = false;
			}
			
			if(!is) {
				return false;
			}
		}
		return true;
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
	
	public static Map newMap(Object key,Object val){
		Map map = new LinkedHashMap();
		map.put(key,val);
		
		return map;
	}
}
