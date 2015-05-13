package com.maliang.core.ui.controller;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.maliang.core.dao.ObjectMetadataDao;
import com.maliang.core.model.ObjectField;
import com.maliang.core.model.ObjectMetadata;

@Controller
@RequestMapping(value = "metadata")
public class ObjectMetadataController {

	private ObjectMetadataDao metadataDao = new ObjectMetadataDao();
	// @RequestMapping(value = "/core/test.json", headers =
	// "Accept=application/json")
	@RequestMapping(value = "edit.htm", method = RequestMethod.GET)
	public String edit(String id,Model model) {
		
		String resultJson = "";
		if(id != null && !id.trim().isEmpty()){
			ObjectMetadata metadata = metadataDao.getByID(id);
			model.addAttribute("metadata", metadata);
			
			resultJson = toJson(metadata);
		}
		model.addAttribute("resultJson", resultJson);
		
		return "/metadata/edit";
	}

	@RequestMapping(value = "save.htm")
	public String save(HttpServletRequest request,Model model) {
		ObjectMetadata data = modelObject(request, "metadata", ObjectMetadata.class);
		List<ObjectField> fields = modelList(request,"field",ObjectField.class);
		data.setFields(fields);
		
		metadataDao.save(data);
		
		return list(model);
	}
	
	@RequestMapping(value = "list.htm")
	public String list(Model model) {
		List<ObjectMetadata> metadataList = metadataDao.list();
		
		List<Map> datas = new ArrayList<Map>();
		for(ObjectMetadata data:metadataList){
			Map result = new HashMap();
			result.put("id", data.getId().toString());
			result.put("name", data.getName());
			result.put("label", data.getLabel());
			
			datas.add(result);
		}
		
		Map resultMap = new HashMap();
		resultMap.put("metadataList", datas);
		
		model.addAttribute("resultJson", JSONObject.fromObject(resultMap).toString());
		return "/metadata/list";
	}
	
	@RequestMapping(value = "detail.htm")
	public String detail(String id,Model model) {
		ObjectMetadata metadata = metadataDao.getByID(id);
		
		//ObjectMetadata metadata = new ObjectMetadataDao().getByID("55504c39d27702a4071e0fd8");
		String resultJson = toJson(metadata);
		
		model.addAttribute("resultJson", resultJson);
		
		return "/metadata/detail";
	}
	
	@RequestMapping(value = "delete.htm")
	public String delete(String id,Model model) {
		metadataDao.remove(id);
		
		return list(model);
	}
	
	private static String toJson(ObjectMetadata metadata){
		Map result = new HashMap();
		result.put("id", metadata.getId().toString());
		result.put("name", metadata.getName());
		result.put("label", metadata.getLabel());
		
		List fields = new ArrayList();
		result.put("fields", fields);
		for(ObjectField of:metadata.getFields()){
			Map map = new HashMap();
			map.put("name", of.getName());
			map.put("label", of.getLabel());
			map.put("type", of.getType());
			
			fields.add(map);
		}
		
		Map model = new HashMap();
		model.put("metadata", result);
		
		return JSONObject.fromObject(model).toString();
	}
	public static void main(String[] args) {
		ObjectMetadata metadata = new ObjectMetadataDao().getByID("55504c39d27702a4071e0fd8");
		
		System.out.println(toJson(metadata));
	}
	
	private static <T> List<T> modelList(HttpServletRequest request, String objName,
			Class<T> cls) {
		Enumeration<String> paramNames = request.getParameterNames();
		
		try {
			List<T> resultList = new ArrayList<T>();
			
			BeanInfo beanInfo = Introspector.getBeanInfo(cls);
			PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
			while (paramNames.hasMoreElements()) {
				String pname = paramNames.nextElement().trim();
				if (!pname.startsWith(objName + "."))
					continue;
				
				String fieldName = pname.substring(objName.length() + 1,pname.length());
				String[] fieldValue = request.getParameterValues(pname);
				
				int i = 0;
				for(String fvalue:fieldValue){
					if(fvalue == null || fvalue.trim().isEmpty()){
						i++;
						continue;
					}
					
					T result = null;
					if(resultList.size()>i){
						result = resultList.get(i);
					}else {
						result = cls.newInstance();
						resultList.add(result);
					}
					
					fvalue = fvalue.trim();
					setFieldValue(pds,fieldName,fvalue,result);
					i++;
				}
			}
			
			return resultList;
		} catch (Exception e) {
		}
		
		return null;
	}
	
	private static void setFieldValue(PropertyDescriptor[] pds,String fieldName,String fieldValue,Object obj){
		for (PropertyDescriptor pd : pds) {
			if (pd.getName().equals(fieldName)) {
				Object value = fieldValue;
				String propertyTypeName = pd.getPropertyType().getSimpleName();
				
				if(pd.getName().equals("id")){
					value = new ObjectId(fieldValue);
				}else if (propertyTypeName.equals("Integer") || propertyTypeName.equals("int")) {
					try {
						value = new Integer(fieldValue);
					}catch(Exception e){
						value = null;
					}
				} else if (propertyTypeName.equals("Double") || propertyTypeName.equals("double")) {
					try {
						value = new Double(fieldValue);
					}catch(Exception e){
						value = null;
					}
				}
				
				try {
					pd.getWriteMethod().invoke(obj, value);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				return;
			}
		}
	}
	

	
	private static <T> T modelObject(HttpServletRequest request, String objName,
			Class<T> cls) {
		Enumeration<String> paramNames = request.getParameterNames();

		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(cls);
			T result = cls.newInstance();
			PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
			while (paramNames.hasMoreElements()) {
				String pname = paramNames.nextElement().trim();
				if (!pname.startsWith(objName + "."))
					continue;

				String fieldName = pname.substring(objName.length() + 1,pname.length());
				String fieldValue = request.getParameter(pname);
				
				setFieldValue(pds,fieldName,fieldValue,result);
			}
			
			return result;
		} catch (Exception e) {
			return null;
		}
	}
}
