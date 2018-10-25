package com.model.controller;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.bson.types.ObjectId;
import org.springframework.ui.Model;

import com.model.service.AdminService;
import com.model.service.DeviceModelService;
import com.model.service.DictService;
import com.model.service.DictTypeService;
import com.model.service.ModelService;
import com.model.service.ModelTypeService;
import com.model.service.Pager;
import com.model.service.impl.AdminServiceImpl;
import com.model.service.impl.DeviceModelServiceImpl;
import com.model.service.impl.DictServiceImpl;
import com.model.service.impl.DictTypeServiceImpl;
import com.model.service.impl.ModelServiceImpl;
import com.model.service.impl.ModelTypeServiceImpl;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.JSONUtils;

public class BasicController {
	protected static JsonConfig defaultJsonConfig = new JsonConfig();
	protected ModelTypeService typeService = new ModelTypeServiceImpl();
	protected DeviceModelService deviceService = new DeviceModelServiceImpl();
	protected AdminService adminService = new AdminServiceImpl();
	protected ModelService modelService = new ModelServiceImpl();
	protected DictTypeService dictTypeService = new DictTypeServiceImpl();
	protected DictService dictService = new DictServiceImpl();
	
	static {
		JSONUtils.getMorpherRegistry().registerMorpher(new ObjectIdMorpher());
		defaultJsonConfig.registerJsonValueProcessor(ObjectId.class, new TOStringProcessor());
	}
	
	protected Pager readPager(HttpServletRequest request) {
		int curPage = 1;
		try {
			curPage = Integer.parseInt(request.getParameter("curPage"));
		}catch(Exception e) {
			curPage = 1;
		}
		
		Pager page = new Pager();
		page.setCurPage(curPage);
		
		return page;
	}
	
	protected String toJSONString(Object obj){
		return JSONObject.fromObject(obj,defaultJsonConfig).toString();
	}
	
	protected JSONObject toJSON(Object obj){
		return JSONObject.fromObject(obj,defaultJsonConfig);//.toString();
	}
	
	protected JSONArray toJSONArray(List obj){
		return JSONArray.fromObject(obj,defaultJsonConfig);
	}
	
	protected String defaultPage(Model model,String page) {
		model.addAttribute("mainInclude", page);
		return "/model/admin/main";
	}
	
	protected static <T extends Object> T readMongodbModel(HttpServletRequest request,String reqName,Class<T> cls){
		Map<String, Object> reqMap = readRequestMap(request);
		JSONObject json = JSONObject.fromObject(reqMap.get(reqName));

		Map<String,Class> cmap = readClassMap(cls);
		
		System.out.println("--------- cmap : " + cmap);
		
		return (T)JSONObject.toBean(json, cls,readClassMap(cls));
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
			        ParameterizedType pt = (ParameterizedType) cls.getDeclaredField(pname).getGenericType();// 获取列表的类型
					if(pt != null && pt.getActualTypeArguments() != null && pt.getActualTypeArguments().length > 0){
						cm.put(pname, (Class)pt.getActualTypeArguments()[0]);
					}
				}catch(Exception e){}
			}
		} catch (Exception e) {}
		
		return cm;
	}
	
	protected static Map<String, Object> readRequestMap(HttpServletRequest request) {
		JSONObject json = JSONObject.fromObject(request.getParameterMap());
		List<String> reqNames = json.names();

		Map<String, Object> reqMap = new HashMap<String, Object>();
		if (reqNames == null || reqNames.size() == 0) {
			return reqMap;
		}
		
		

		for (String reqName : reqNames) {
			JSONArray ja = (JSONArray) json.get(reqName);
			
			Object reqValue = null;
			if(ja.size() == 1){
				reqValue = ((JSONArray) json.get(reqName)).get(0);
			}else if(ja.size() > 1){
				reqValue = ja;
			}
			if (reqValue == null)
				continue;

			if(reqName.endsWith("[]")){
				reqName = reqName.substring(0,reqName.length()-2);
			}
			
			setValue(reqMap, reqName, reqValue);
		}

		return reqMap;
	}
	
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
}
