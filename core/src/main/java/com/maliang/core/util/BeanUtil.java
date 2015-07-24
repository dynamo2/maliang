package com.maliang.core.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

public class BeanUtil {
	
	public static Object readValue(Object obj,String fname){
		Object value = readProperty(obj,fname);
		if(value == null){
			return methodValue(obj,fname,null);
		}
		
		return value;
	}
	
	public static Object readProperty(Object obj,String fname){
		return readProperty(obj,fname,null);
	}
	
	public static Object readProperty(Object obj,String fname,Object defaultValue){
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
			PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
			for(PropertyDescriptor pd :pds){
				if(pd.getName().equals(fname)){
					return pd.getReadMethod().invoke(obj);
				}
			}
		} catch (Exception e) {}
		
		return defaultValue;
	}
	
	public static Object methodValue(Object obj,String mName,Object defaultValue){
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
			MethodDescriptor[] mds = beanInfo.getMethodDescriptors();
			for(MethodDescriptor md :mds){
				if(md.getName().equals(mName)){
					return md.getMethod().invoke(obj);
				}
			}
		} catch (Exception e) {}
		
		return defaultValue;
	}
	
	public static void main(String[] args) {
		List ls = new ArrayList(10);
		ls.add("ddd");
		
		Object v = methodValue(ls,"size",null);
		v = readValue(ls,"size");
		System.out.println(ls.size() + "="+v);
	}
}
