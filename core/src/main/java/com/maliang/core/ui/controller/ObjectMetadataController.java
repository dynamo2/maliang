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
import org.springframework.web.bind.annotation.ResponseBody;

import com.maliang.core.dao.ObjectMetadataDao;
import com.maliang.core.model.FieldType;
import com.maliang.core.model.ObjectField;
import com.maliang.core.model.ObjectMetadata;

@Controller
@RequestMapping(value = "metadata")
public class ObjectMetadataController {

	private ObjectMetadataDao metadataDao = new ObjectMetadataDao();

	// @RequestMapping(value = "/core/test.json", headers =
	// "Accept=application/json")
	@RequestMapping(value = "edit.htm", method = RequestMethod.GET)
	public String edit(String id, Model model) {

		String resultJson = "";
		if (id != null && !id.trim().isEmpty()) {
			ObjectMetadata metadata = metadataDao.getByID(id);
			model.addAttribute("metadata", metadata);

			resultJson = toEditJson(metadata);
		}else {
			resultJson = toEditJson();
		}
		
		model.addAttribute("resultJson", resultJson);

		return "/metadata/edit";
	}

	@RequestMapping(value = "save.htm")
	public String save(HttpServletRequest request, Model model) {
		System.out.println("maxIndex : " + request.getParameter("maxIndex"));
		ObjectMetadata data = modelObject(request, "metadata",
				ObjectMetadata.class);
		List<ObjectField> fields = modelList(request, "field",
				ObjectField.class);
		data.setFields(fields);
		data.setUniqueMark(data.getName());
		
		System.out.println(data);

		metadataDao.save(data);

		return list(model);
	}

	@RequestMapping(value = "list.htm")
	public String list(Model model) {
		List<ObjectMetadata> metadataList = metadataDao.list();

		List<Map> datas = new ArrayList<Map>();
		for (ObjectMetadata data : metadataList) {
			Map result = new HashMap();
			result.put("id", data.getId().toString());
			result.put("name", data.getName());
			result.put("label", data.getLabel());

			datas.add(result);
		}

		Map resultMap = new HashMap();
		resultMap.put("metadataList", datas);

		model.addAttribute("resultJson", JSONObject.fromObject(resultMap)
				.toString());
		return "/metadata/list";
	}

	@RequestMapping(value = "detail.htm")
	public String detail(String id, Model model) {
		ObjectMetadata metadata = metadataDao.getByID(id);
		String resultJson = toJson(metadata);

		model.addAttribute("resultJson", resultJson);

		return "/metadata/detail";
	}

	@RequestMapping(value = "delete.htm")
	public String delete(String id, Model model) {
		metadataDao.remove(id);

		return list(model);
	}

	public static Map<String, Object> linkedObjectMap(
			List<ObjectMetadata> metadataList, ObjectField ofield) {
		Object value = ofield != null ? ofield.getLinkedObject() : "";

		Map<String, Object> objMap = new HashMap<String, Object>();
		objMap.put("label", "");

		Map<String, Object> objInfoMap = new HashMap<String, Object>();
		objMap.put("info", objInfoMap);

		objInfoMap.put("name", "field.linkedObject");
		objInfoMap.put("type", "select");
		objInfoMap.put("value", value);

		List<Map> options = new ArrayList<Map>();
		for (ObjectMetadata data : metadataList) {
			Map result = new HashMap();
			result.put("label", data.getName());
			result.put("key", data.getId().toString());

			options.add(result);
		}
		objInfoMap.put("options", options);

		return objMap;
	}

	public static Map<String, Object> relationshipMap(ObjectField ofield) {
		Object value = ofield != null ? ofield.getRelationship() : "";

		Map<String, Object> objInfoMap = new HashMap<String, Object>();
		objInfoMap.put("name", "field.relationship");
		objInfoMap.put("type", "select");
		objInfoMap.put("value", value);

		List<Map> options = new ArrayList<Map>();
		Map result = new HashMap();
		result.put("label", "1对多");
		result.put("key", "1");
		options.add(result);

		result = new HashMap();
		result.put("label", "1对1");
		result.put("key", "2");
		options.add(result);

		objInfoMap.put("options", options);

		Map<String, Object> objMap = new HashMap<String, Object>();
		objMap.put("label", "");
		objMap.put("info", objInfoMap);

		return objMap;
	}

	private static String toEditJson() {
		Map result = new HashMap();

		result.put("id", "");
		result.put("name", "");
		result.put("label", "");

		List<ObjectMetadata> metadataList = new ObjectMetadataDao().list();

		List fields = new ArrayList();
		result.put("fields", fields);
		
		
		Map<String, Object> fieldsMap = new HashMap<String, Object>();

		Map<String, Object> fieldMap = new HashMap<String, Object>();
		fieldsMap.put("name", fieldMap);
		fieldMap.put("label", "名称");

		Map<String, Object> infoMap = new HashMap<String, Object>();
		infoMap.put("name", "field.name");
		infoMap.put("value", "");
		infoMap.put("type", "text");
		fieldMap.put("info", infoMap);

		fieldMap = new HashMap<String, Object>();
		fieldsMap.put("label", fieldMap);
		fieldMap.put("label", "标签");

		infoMap = new HashMap<String, Object>();
		infoMap.put("name", "field.label");
		infoMap.put("value", "");
		infoMap.put("type", "text");
		fieldMap.put("info", infoMap);

		fieldMap = new HashMap<String, Object>();
		fieldsMap.put("type", fieldMap);
		fieldMap.put("label", "类型");

		infoMap = new HashMap<String, Object>();
		infoMap.put("name", "field.type");
		infoMap.put("value", "");
		infoMap.put("type", "select");

		List<Map<String, Object>> types = new ArrayList<Map<String, Object>>();
		for (FieldType type : FieldType.values()) {
			Map<String, Object> tmap = new HashMap<String, Object>();
			tmap.put("label", type.getName());
			tmap.put("key", type.getCode());

			types.add(tmap);
		}
		infoMap.put("options", types);
		fieldMap.put("info", infoMap);

		fieldsMap.put("linkedObject", linkedObjectMap(metadataList, null));
		fieldsMap.put("relationship", relationshipMap(null));

		fields.add(fieldsMap);

		Map model = new HashMap();
		model.put("metadata", result);

		return JSONObject.fromObject(model).toString();
	}

	private static String toEditJson(ObjectMetadata metadata) {
		Map result = new HashMap();

		// String json =
		// "{"label":"名称","type":"text","info":{"name":"Product.name","value":"天气丹华泫平衡化妆水150ml"}}";
		// {"label":"分类","type":"select","info":{"name":"Product.product_type","options":[
		// {"label":"洁面","key":"552e3362d27776926941e009"},{"label":"卸妆","key":"552e3362d27776926941e00a"},{"label":"爽肤水","key":"552e3362d27776926941e00b"},{"label":"保湿水","key":"552e3362d27776926941e00c"},{"label":"精华","key":"552e3362d27776926941e00d"},{"label":"面霜/乳液","key":"552e3362d27776926941e00e"},{"label":"眼霜","key":"552e3362d27776926941e00f"},{"label":"面膜","key":"552e3362d27776926941e010"},{"label":"去角质产品","key":"552e3362d27776926941e011"}]

		result.put("id", metadata.getId().toString());
		result.put("name", metadata.getName());
		result.put("label", metadata.getLabel());

		List<ObjectMetadata> metadataList = new ObjectMetadataDao().list();

		List fields = new ArrayList();
		result.put("fields", fields);
		for (ObjectField of : metadata.getFields()) {
			Map<String, Object> fieldsMap = new HashMap<String, Object>();

			Map<String, Object> fieldMap = new HashMap<String, Object>();
			fieldsMap.put("name", fieldMap);
			fieldMap.put("label", "名称");

			Map<String, Object> infoMap = new HashMap<String, Object>();
			infoMap.put("name", "field.name");
			infoMap.put("value", of.getName());
			infoMap.put("type", "text");
			fieldMap.put("info", infoMap);

			fieldMap = new HashMap<String, Object>();
			fieldsMap.put("label", fieldMap);
			fieldMap.put("label", "标签");

			infoMap = new HashMap<String, Object>();
			infoMap.put("name", "field.label");
			infoMap.put("value", of.getLabel());
			infoMap.put("type", "text");
			fieldMap.put("info", infoMap);

			fieldMap = new HashMap<String, Object>();
			fieldsMap.put("type", fieldMap);
			fieldMap.put("label", "类型");

			infoMap = new HashMap<String, Object>();
			infoMap.put("name", "field.type");
			infoMap.put("value", of.getType());
			infoMap.put("type", "select");

			List<Map<String, Object>> types = new ArrayList<Map<String, Object>>();
			for (FieldType type : FieldType.values()) {
				Map<String, Object> tmap = new HashMap<String, Object>();
				tmap.put("label", type.getName());
				tmap.put("key", type.getCode());

				types.add(tmap);
			}
			infoMap.put("options", types);
			fieldMap.put("info", infoMap);

			fieldsMap.put("linkedObject", linkedObjectMap(metadataList, of));
			fieldsMap.put("relationship", relationshipMap(of));

			fields.add(fieldsMap);
		}

		Map model = new HashMap();
		model.put("metadata", result);

		return JSONObject.fromObject(model).toString();
	}

	private static String toJson(ObjectMetadata metadata) {
		Map result = new HashMap();
		result.put("id", metadata.getId().toString());
		result.put("name", metadata.getName());
		result.put("label", metadata.getLabel());

		List fields = new ArrayList();
		result.put("fields", fields);
		for (ObjectField of : metadata.getFields()) {
			Map map = new HashMap();
			map.put("name", of.getName());
			map.put("label", of.getLabel());
			map.put("type", of.getType());
			map.put("linkedObject", of.getLinkedObject());
			map.put("relationship", of.getRelationship());

			fields.add(map);
		}

		Map model = new HashMap();
		model.put("metadata", result);

		return JSONObject.fromObject(model).toString();
	}

	public static void main(String[] args) {
		ObjectMetadata metadata = new ObjectMetadataDao()
				.getByID("55504baad277e12f364ffb40");

		System.out.println(toEditJson(metadata));
	}
	
	private static Integer getInt(HttpServletRequest request,String reqName,int dev){
		try {
			return Integer.valueOf(request.getParameter(reqName));
		}catch(Exception e){
			return dev;
		}
	}

	private static <T> List<T> modelList(HttpServletRequest request,
			String objName, Class<T> cls) {
		Enumeration<String> paramNames = request.getParameterNames();
		
		Integer maxIndex = getInt(request,"maxIndex",-1);
		try {
			List<T> resultList = new ArrayList<T>();

			BeanInfo beanInfo = Introspector.getBeanInfo(cls);
			PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
			
			if(maxIndex > 0){
				for(int i = 0; i <= maxIndex; i++){
					T result = null;
					for(PropertyDescriptor pd : pds){
						if(pd.getName().equals("class"))continue;
						
						String reqName = i+"."+objName+"."+pd.getName();
						String reqValue = request.getParameter(reqName);
						if(reqValue != null){
							if(result == null){
								result = cls.newInstance();
								resultList.add(result);
							}
							
							setFieldValue(pd,reqValue,result);
						}
					}
				}
			}
			
			
			/*
			while (paramNames.hasMoreElements()) {
				String pname = paramNames.nextElement().trim();
				if (!pname.startsWith(objName + "."))
					continue;

				String fieldName = pname.substring(objName.length() + 1,
						pname.length());
				String[] fieldValue = request.getParameterValues(pname);

				int i = 0;
				for (String fvalue : fieldValue) {
					if (fvalue == null || fvalue.trim().isEmpty()) {
						i++;
						continue;
					}

					T result = null;
					if (resultList.size() > i) {
						result = resultList.get(i);
					} else {
						result = cls.newInstance();
						resultList.add(result);
					}

					fvalue = fvalue.trim();
					setFieldValue(pds, fieldName, fvalue, result);
					i++;
				}
			}*/

			return resultList;
		} catch (Exception e) {
		}

		return null;
	}

	private static void setFieldValue(PropertyDescriptor[] pds,
			String fieldName, String fieldValue, Object obj) {
		for (PropertyDescriptor pd : pds) {
			if (pd.getName().equals(fieldName)) {
				Object value = fieldValue;
				String propertyTypeName = pd.getPropertyType().getSimpleName();

				if (pd.getName().equals("id")) {
					try {
						value = new ObjectId(fieldValue);
					}catch(Exception e){
						value = null;
					}
					
				} else if (propertyTypeName.equals("Integer")
						|| propertyTypeName.equals("int")) {
					try {
						value = new Integer(fieldValue);
					} catch (Exception e) {
						value = null;
					}
				} else if (propertyTypeName.equals("Double")
						|| propertyTypeName.equals("double")) {
					try {
						value = new Double(fieldValue);
					} catch (Exception e) {
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
	
	private static void setFieldValue(PropertyDescriptor pd,String fieldValue, Object obj) {
		Object value = fieldValue;
		String propertyTypeName = pd.getPropertyType().getSimpleName();

		if (pd.getName().equals("id")) {
			try {
				value = new ObjectId(fieldValue);
			}catch(Exception e){
				value = null;
			}
			
		} else if (propertyTypeName.equals("Integer")
				|| propertyTypeName.equals("int")) {
			try {
				value = new Integer(fieldValue);
			} catch (Exception e) {
				value = null;
			}
		} else if (propertyTypeName.equals("Double")
				|| propertyTypeName.equals("double")) {
			try {
				value = new Double(fieldValue);
			} catch (Exception e) {
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

	private static <T> T modelObject(HttpServletRequest request,
			String objName, Class<T> cls) {
		Enumeration<String> paramNames = request.getParameterNames();

		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(cls);
			T result = cls.newInstance();
			PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
			while (paramNames.hasMoreElements()) {
				String pname = paramNames.nextElement().trim();
				if (!pname.startsWith(objName + "."))
					continue;

				String fieldName = pname.substring(objName.length() + 1,
						pname.length());
				String fieldValue = request.getParameter(pname);

				setFieldValue(pds, fieldName, fieldValue, result);
			}

			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
