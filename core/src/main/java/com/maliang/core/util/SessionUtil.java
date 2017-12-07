package com.maliang.core.util;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.maliang.core.model.Business;
import com.maliang.core.model.Workflow;

public class SessionUtil {
	public final static String BUSINESS = "SYS_BUSINESS";
	public final static String FLOW = "CURRENT_WORKFLOW";
	//public final static String BUSINESS = "SYS_BUSINESS";
	
	public static Business getBusiness(){
		try {
			Object val = getValue(BUSINESS);
			if(val != null && val instanceof Business){
				return (Business)val;
			}
		}catch(Exception e){}
		
		return null;
	}
	
	public static Workflow getFlow(){
		try {
			Object val = getValue(FLOW);
			if(val != null && val instanceof Workflow){
				return (Workflow)val;
			}
		}catch(Exception e){}
		
		return null;
	}
	
	public static Object getValue(String key){
		return getSession().getAttribute(key);
	}
	
	public static void put(String key,Object val) {
		getSession().setAttribute(key,val);
	}
	
	public static void putAll(Map<String,Object> vals) {
		if(!Utils.isEmpty(vals)){
			for(String k : vals.keySet()){
				put(k,vals.get(k));
			}
		}
	}
	
	public static void put(HttpServletRequest request,String key,Object val) {
		request.getSession().setAttribute(key,val);
	}
	
	public static HttpSession getSession(){
		try {
			return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getSession();
		}catch(Exception e){
			return null;
		}
	}
}
