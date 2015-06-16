package com.maliang.core.ui.controller;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.bson.types.ObjectId;

import com.maliang.core.model.Mapped;

public class BasicController {
	public Integer getInt(String v){
		try {
			return Integer.valueOf(v);
		}catch(Exception e){
			return null;
		}
	}
	
	public Double getDouble(String v){
		try {
			return Double.valueOf(v);
		}catch(Exception e){
			return null;
		}
	}
	
	public ObjectId getObjectId(String v){
		try {
			return new ObjectId(v);
		}catch(Exception e){
			return null;
		}
	}
	
	public Integer getInt(HttpServletRequest request,String name){
		return this.getInt(request, name,0);
	}
	
	public Integer getInt(HttpServletRequest request,String name,int dev){
		try {
			return Integer.valueOf(request.getParameter(name));
		}catch(Exception e){
			return dev;
		}
	}
	
	protected <T> T buildToObject(Map<String,Object> objMap,Class<T> cls) {
		if(objMap == null || objMap.isEmpty())return null;
		
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(cls);
			PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
			T result = cls.newInstance();
			for(PropertyDescriptor pd:pds){
				String fieldName = pd.getName();
				Object fieldValue = objMap.get(fieldName);
				if("class".equals(fieldName))continue;
				if(fieldValue == null)continue;
				
				if(fieldValue instanceof List) {
					Mapped anno = cls.getDeclaredField(fieldName).getAnnotation(Mapped.class);
					Class linkClass = anno.type();
					
					List vlist = new ArrayList();
					List<Map<String,Object>> colValue = (List<Map<String,Object>>)fieldValue;
					for(Map<String,Object> map : colValue){
						vlist.add(buildToObject(map,linkClass));
					}
					
					fieldValue = vlist;
				}else if("java.lang.Integer".equals(pd.getPropertyType().getCanonicalName())){
					fieldValue = this.getInt(fieldValue.toString());
				}else if("java.lang.Double".equals(pd.getPropertyType().getCanonicalName())){
					fieldValue = this.getDouble(fieldValue.toString());
				}else if("org.bson.types.ObjectId".equals(pd.getPropertyType().getCanonicalName())){
					fieldValue = this.getObjectId(fieldValue.toString());
				}
				
				pd.getWriteMethod().invoke(result, fieldValue);
			}
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		return null;
	}
	
	protected Map<String,Object> readRequestMap(HttpServletRequest request){
		Enumeration<String> reqNames = request.getParameterNames();
		Map<String,Object> reqMap = new HashMap<String,Object>();
		List<String> ignorePrefix = new ArrayList<String>();
		while(reqNames.hasMoreElements()){
			String reqName = reqNames.nextElement();
			
			if(reqName.endsWith(".maxIndex")){
				String prefix = reqName.substring(0,reqName.length()-".maxIndex".length());
				readList(request,prefix,reqMap);
				ignorePrefix.add(prefix);
			}
		}
		
		reqNames = request.getParameterNames();
		while(reqNames.hasMoreElements()){
			String reqName = reqNames.nextElement();
			boolean ingore = false;
			for(String inPre:ignorePrefix){
				if(reqName.startsWith(inPre)){
					ingore = true;
					break;
				}
			}
			if(ingore)continue;
			
			Object reqValue = request.getParameter(reqName);
			if(reqValue == null)continue;
			
			String[] reqs = reqName.split("\\.");
			Map<String,Object> parentMap = reqMap;
			for(int i = 0; i < reqs.length-1; i++){
				String req = reqs[i];
				Map<String,Object> preMap = (Map<String,Object>)parentMap.get(req);
				if(preMap == null){
					preMap = new HashMap<String,Object>();
					parentMap.put(req, preMap);
				}
				parentMap = preMap;
			}
			parentMap.put(reqs[reqs.length-1], reqValue);
		}
		
		return reqMap;
	}
	
	protected void readList(HttpServletRequest request,String prefix,Map<String,Object> reqMap){
		String[] pres = prefix.split("\\.");
		
		Map<String,Object> parentMap = reqMap;
		for(int i = 0; i < pres.length-1; i++){
			String pre = pres[i];
			Map<String,Object> preMap = new HashMap<String,Object>();
			
			parentMap.put(pre, preMap);
			parentMap = preMap;
		}
		
		List<Map<String,Object>> preList = new ArrayList<Map<String,Object>>();
		parentMap.put(pres[pres.length-1], preList);
		
		TreeMap<Integer,Map<String,Object>> preMaps = new TreeMap<Integer,Map<String,Object>>();
		Enumeration<String> reqNames = request.getParameterNames();
		prefix = prefix+".";
		while(reqNames.hasMoreElements()){
			String reqName = reqNames.nextElement();
			if(!reqName.startsWith(prefix))continue;
			if(reqName.equals(prefix+"maxIndex"))continue;
			
			Object reqValue = request.getParameter(reqName);
			if(reqValue == null)continue;
			
			String subName = reqName.substring(prefix.length(),reqName.length());
			String[] subs = subName.split("\\.");
			Integer index = Integer.valueOf(subs[0]);
			
			Map<String,Object> preMap = preMaps.get(index);
			if(preMap == null){
				preMap = new HashMap<String,Object>();
				preMaps.put(index, preMap);
			}
			preMap.put(subs[1], request.getParameter(reqName));
		}
		
		preList.addAll(preMaps.values());
	}
	
	@SuppressWarnings("rawtypes")
	protected static Map<String,Object> buildInputsMap(Object obj,Map<String,Map<String,String>> labelMap,String prefix) {
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
			PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
			
			Map<String,Object> objMap = new HashMap<String,Object>();
			for(PropertyDescriptor pd : pds){
				if(pd.getName().equals("class"))continue;
				
				Map<String,Object> fieldMap = new HashMap<String,Object>();
				
				String fieldName = pd.getName();
				String fieldType = pd.getPropertyType().getCanonicalName();
				Object fieldValue = pd.getReadMethod().invoke(obj);
				
				String inputType = "textarea";
				if(fieldType.equals("java.util.List")){
					String itemPrefix = prefix+"."+fieldName;
					fieldMap.put("item-prefix",itemPrefix);
					
					List<Map<String,Object>> itemInputs = new ArrayList<Map<String,Object>>();
					Map<String,String> itemLabelMap = null;
					if(fieldValue != null){
						List objItems = (List)fieldValue;
						for(Object oi : objItems){
							if(itemLabelMap == null){
								itemLabelMap = labelMap.get(oi.getClass().getCanonicalName());
							}
							
							itemInputs.add(buildInputsMap(oi,labelMap,itemPrefix));
						}
					}else {
						Mapped mappedAnno = obj.getClass().getDeclaredField(fieldName).getAnnotation(Mapped.class);
						if(mappedAnno != null){
							Class linkedClass = mappedAnno.type();
							if(linkedClass != null){
								itemLabelMap = labelMap.get(linkedClass.getCanonicalName());
								itemInputs.add(buildInputsMap(linkedClass.newInstance(),labelMap,itemPrefix));
							}
						}
					}
					fieldMap.put("item-labels",itemLabelMap);
					fieldValue = itemInputs;
					inputType = "list";
				}else if(fieldType.equals("java.lang.Integer")){
					inputType = "number";
				}else if(fieldType.equals("java.lang.Double")){
					inputType = "number";
				}else if(fieldType.equals("java.util.Date")){
					inputType = "date";
				}

				if(fieldName.equals("id")){
					inputType = "hidden";
					if(fieldValue != null){
						fieldValue = fieldValue.toString();
					}
				}
				
				fieldMap.put("type",inputType);
				fieldMap.put("value",fieldValue);
				
				String label = null;
				if(labelMap != null){
					Map<String,String> map = (Map<String,String>)labelMap.get(obj.getClass().getCanonicalName());
					if(map != null){
						label = map.get(fieldName);
					}
				}
				fieldMap.put("label",label);
				fieldMap.put("prefix",prefix);
				fieldMap.put("name",fieldName);
				
				objMap.put(fieldName, fieldMap);
			}
			
			return objMap;
		} catch (Exception e) {
			return null;
		}
	}
}
