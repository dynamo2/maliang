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

import net.sf.ezmorph.object.AbstractObjectMorpher;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;
import net.sf.json.util.JSONUtils;

import org.bson.types.ObjectId;

import com.maliang.core.dao.BusinessDao;
import com.maliang.core.dao.ObjectMetadataDao;
import com.maliang.core.dao.ProjectDao;
import com.maliang.core.dao.UCTypeDao;
import com.maliang.core.model.Mapped;
import com.maliang.core.model.MongodbModel;
import com.maliang.core.model.TriggerAction;
import com.maliang.core.util.StringUtil;
import com.maliang.core.util.Utils;

public class BasicController {
	protected static JsonConfig defaultJsonConfig = new JsonConfig();
	
	protected ObjectMetadataDao metadataDao = new ObjectMetadataDao();
	protected BusinessDao businessDao = new BusinessDao();
	protected UCTypeDao uctypeDao = new UCTypeDao();
	protected ProjectDao projectDao = new ProjectDao();
	
	static {
		JSONUtils.getMorpherRegistry().registerMorpher(new ObjectIdMorpher());
		defaultJsonConfig.registerJsonValueProcessor(ObjectId.class, new TOStringProcessor());
	}
	
	protected static <T extends MongodbModel> T readMongodbModel(HttpServletRequest request,String reqName,Class<T> cls){
		JSONObject json = JSONObject.fromObject(request.getParameterMap());
		JSONArray ja = (JSONArray)json.get(reqName);
		
		Map<String,Class> cmap = readClassMap(cls);
		System.out.println("****************** class map : "+cmap);

		return (T)JSONObject.toBean(ja.getJSONObject(0), cls,readClassMap(cls));
	}
	
	protected static Map<String,Class> readClassMap(Class cls){
		Map<String,Class> cm = new HashMap<String,Class>();
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(cls);
			PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
			
			for(PropertyDescriptor pd : pds){
				String pname = pd.getName();
				if("class".equals(pname))continue;
				
				try {
					Mapped mapped = cls.getDeclaredField(pname).getAnnotation(Mapped.class);
					if(mapped != null){
						cm.put(pname, mapped.type());
					}
				}catch(Exception e){}
			}
		} catch (Exception e) {}
		
		return cm;
	}
	
	protected static Map<String,Object> newMap(String key,Object val){
		Map<String,Object> mps = new HashMap<String,Object>();
		mps.put(key, val);
		return mps;
	}
	
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
	
	protected Map<String, Object> readRequestMapNotJSONFilter(
			HttpServletRequest request) {
		// JSONObject json = JSONObject.fromObject(request.getParameterMap());
		Enumeration<String> reqNames = request.getParameterNames();
		Map<String, Object> reqMap = new HashMap<String, Object>();
		while (reqNames.hasMoreElements()) {
			String reqName = reqNames.nextElement();
			Object reqValue = request.getParameter(reqName);
			if (reqValue == null)
				continue;

			setValue(reqMap, reqName, reqValue);
		}

		return reqMap;
	}
	
	/***
	 * 解析数组表单数据的格式：
	 * 		cookbook.motherStock.food,cookbook.motherStock.weight
	 * 
	 * ****/
	protected static void setValue222(Map<String, Object> rootMap, String reqName,
			Object reqValue) {
		if (reqName == null)
			return;

		Object lastKey = null;
		Object lastParent = rootMap;
		Object parent = rootMap;
		String[] parents = reqName.split("\\.");
		for (int i = 0; i < parents.length; i++) {
			String name = parents[i];
			boolean last = i == parents.length - 1;

			Integer index = null;
			try {
				index = Integer.parseInt(name);
			} catch (Exception e) {
				index = null;
			}

			if (index != null && index >= 0) {
				List list = null;
				if (parent instanceof List) {
					list = (List) parent;
				} else {
					list = new ArrayList();

					if (lastParent instanceof Map) {
						((Map) lastParent).put(lastKey, list);
					} else if (lastParent instanceof List) {
						if (lastKey instanceof Integer) {
							((List) lastParent).set((Integer) lastKey, list);
						}
					}
				}

				if (index >= list.size()) {
					int ii = index - list.size();
					while (ii-- >= 0) {
						list.add(null);
					}
				}

				if (last) {
					list.set(index, reqValue);
					break;
				}

				Object temp = list.get(index);
				if (temp == null) {
					temp = new HashMap();
					list.set(index, temp);
				}

				parent = temp;
				lastParent = list;
				lastKey = index;
			} else {
				if (!(parent instanceof Map)) {
					parent = new HashMap();
					if (lastParent instanceof Map) {
						((Map) lastParent).put(lastKey, parent);
					} else if (lastParent instanceof List
							&& lastKey instanceof Integer) {
						((List) lastParent).set((Integer) lastKey, parent);
					}
				}

				if (last) {
					((Map) parent).put(name, reqValue);
					break;
				}

				Object temp = ((Map) parent).get(name);
				if (temp == null) {
					temp = new HashMap<String, Object>();
					((Map) parent).put(name, temp);
				}

				lastKey = name;
				lastParent = parent;
				parent = temp;
			}
		}
	}

	/***
	 * 解析数组表单数据的格式：
	 * 		cookbook.motherStock.0.food,cookbook.motherStock.0.weight
	 * 
	 * ****/
	protected static void setValue(Map<String, Object> rootMap, String reqName,
			Object reqValue) {
		if (reqName == null)
			return;

		Object lastKey = null;
		Object lastParent = rootMap;
		Object parent = rootMap;
		String[] parents = reqName.split("\\.");
		for (int i = 0; i < parents.length; i++) {
			String name = parents[i];
			boolean last = i == parents.length - 1;

			Integer index = null;
			try {
				index = Integer.parseInt(name);
			} catch (Exception e) {
				index = null;
			}

			if (index != null && index >= 0) {
				List list = null;
				if (parent instanceof List) {
					list = (List) parent;
				} else {
					list = new ArrayList();

					if (lastParent instanceof Map) {
						((Map) lastParent).put(lastKey, list);
					} else if (lastParent instanceof List) {
						if (lastKey instanceof Integer) {
							((List) lastParent).set((Integer) lastKey, list);
						}
					}
				}

				if (index >= list.size()) {
					int ii = index - list.size();
					while (ii-- >= 0) {
						list.add(null);
					}
				}

				if (last) {
					list.set(index, reqValue);
					break;
				}

				Object temp = list.get(index);
				if (temp == null) {
					temp = new HashMap();
					list.set(index, temp);
				}

				parent = temp;
				lastParent = list;
				lastKey = index;
			} else {
				if (!(parent instanceof Map)) {
					parent = new HashMap();
					if (lastParent instanceof Map) {
						((Map) lastParent).put(lastKey, parent);
					} else if (lastParent instanceof List
							&& lastKey instanceof Integer) {
						((List) lastParent).set((Integer) lastKey, parent);
					}
				}

				if (last) {
					((Map) parent).put(name, reqValue);
					break;
				}

				Object temp = ((Map) parent).get(name);
				if (temp == null) {
					temp = new HashMap<String, Object>();
					((Map) parent).put(name, temp);
				}

				lastKey = name;
				lastParent = parent;
				parent = temp;
			}
		}
	}
	
	protected String json(Object obj){
		return JSONObject.fromObject(obj,defaultJsonConfig).toString();
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
				
				String canonicalType = pd.getPropertyType().getCanonicalName();
				if(fieldValue instanceof List) {
					Mapped anno = cls.getDeclaredField(fieldName).getAnnotation(Mapped.class);
					Class linkClass = anno.type();
					
					List vlist = new ArrayList();
					List<Map<String,Object>> colValue = (List<Map<String,Object>>)fieldValue;
					for(Map<String,Object> map : colValue){
						vlist.add(buildToObject(map,linkClass));
					}
					
					fieldValue = vlist;
				}else if("int".equals(canonicalType) || "java.lang.Integer".equals(canonicalType)){
					fieldValue = this.getInt(fieldValue.toString());
				}else if("double".equals(canonicalType) || "java.lang.Double".equals(canonicalType)){
					fieldValue = this.getDouble(fieldValue.toString());
				}else if("java.lang.String".equals(canonicalType)){
					fieldValue = fieldValue.toString();
				}else if("org.bson.types.ObjectId".equals(canonicalType)){
					fieldValue = this.getObjectId(fieldValue.toString());
				}
				
				//debug
				try {
					pd.getWriteMethod().invoke(result, fieldValue);
				} catch (java.lang.IllegalArgumentException e) {
					System.out.println(" error pd : " + pd.getName());
					System.out.println("fieldValue : " + fieldValue.getClass());
					
					throw e;
				}
			}

			return result;
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		return null;
	}
	
	protected Object switchType(Object objVal,Class cls){
		String clsName = cls.getCanonicalName();
		if("int".equals(clsName) || "java.lang.Integer".equals(clsName)){
			return this.getInt(objVal.toString());
		}else if("double".equals(clsName) || "java.lang.Double".equals(clsName)){
			return this.getDouble(objVal.toString());
		}else if("java.lang.String".equals(clsName)){
			return objVal.toString();
		}else if("org.bson.types.ObjectId".equals(clsName)){
			return this.getObjectId(objVal.toString());
		}
		return objVal;
	}
	
	protected Object switchToObject(Object objVal,Class cls) {
		if(objVal == null)return null;

		Map<String,Object> objMap = null;
		if(objVal instanceof Map){
			objMap = (Map<String,Object>)objVal;
		}else {
			return this.switchType(objVal, cls);
		}
		
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(cls);
			PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
			Object result = cls.newInstance();

			for(PropertyDescriptor pd:pds){
				String fieldName = pd.getName();
				Object fieldValue = objMap.get(fieldName);
				if("class".equals(fieldName))continue;
				if(fieldValue == null)continue;
				
				if(Utils.isArray(fieldValue)) {
					Mapped anno = cls.getDeclaredField(fieldName).getAnnotation(Mapped.class);
					Class linkClass = anno.type();
					
					List vlist = new ArrayList();
					Object[] colValue = Utils.toArray(fieldValue);
					for(Object obj : colValue){
						vlist.add(switchToObject(obj,linkClass));
					}
					fieldValue = vlist;
				}else {
					fieldValue = this.switchType(fieldValue, pd.getPropertyType());
				}
				
				//debug
				try {
					pd.getWriteMethod().invoke(result, fieldValue);
				} catch (java.lang.IllegalArgumentException e) {
					System.out.println(" error pd : " + pd.getName());
					System.out.println("fieldValue : " + fieldValue.getClass());
					
					throw e;
				}
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
		
		List<Object> preList = new ArrayList<Object>();
		parentMap.put(pres[pres.length-1], preList);
		
		TreeMap<Integer,Object> preMaps = new TreeMap<Integer,Object>();
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
			String key = null;
			if(subs.length == 2){
				key = subs[1];
			}

			if(StringUtil.isEmpty(key)){
				preMaps.put(index, reqValue);
			}else {
				Map<String,Object> preMap = (Map<String,Object>)preMaps.get(index);
				if(preMap == null){
					preMap = new HashMap<String,Object>();
					preMaps.put(index, preMap);
				}
				preMap.put(key, reqValue);
			}
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

				fieldMap.put("label",readLabel(obj, labelMap, fieldName));
				fieldMap.put("prefix",prefix);
				fieldMap.put("name",fieldName);
				
				objMap.put(fieldName, fieldMap);
			}
			
			return objMap;
		} catch (Exception e) {
			return null;
		}
	}

	private static String readLabel(Object obj,
			Map<String, Map<String, String>> labelMap, String fieldName) {
		if(labelMap != null){
			Map<String,String> map = (Map<String,String>)labelMap.get(obj.getClass().getCanonicalName());
			if(map != null){
				return map.get(fieldName);
			}
		}
		return null;
	}
}

class TOStringProcessor implements JsonValueProcessor{

	public Object processArrayValue(Object arg0, JsonConfig arg1) {
		if(arg0 == null)return null;
		
		return arg0.toString();
	}

	public Object processObjectValue(String arg0, Object arg1,
			JsonConfig arg2) {
		if(arg1 == null)return null;
		
		return arg1.toString();
	}
}

class ObjectIdMorpher extends AbstractObjectMorpher {
	public Class morphsTo() {
		return ObjectId.class;
	}

	public boolean supports(Class c) {
		return true;
	}

	public Object morph(Object v) {
		if(v == null || v instanceof ObjectId){
			return v;
		}
		
		try {
			return new ObjectId(v.toString());
		}catch(Exception e){
			return null;
		}
	}
}