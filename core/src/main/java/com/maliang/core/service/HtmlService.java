package com.maliang.core.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.LinkedMap;

import com.maliang.core.arithmetic.ArithmeticExpression;
import com.maliang.core.dao.CollectionDao;
import com.maliang.core.dao.ObjectMetadataDao;
import com.maliang.core.model.FieldType;
import com.maliang.core.model.ObjectField;
import com.maliang.core.model.ObjectMetadata;

public class HtmlService {
	protected ObjectMetadataDao metaDao = new ObjectMetadataDao();
	protected CollectionDao collDao = new CollectionDao();
	
	/**
	 * htmlSetting:{info:['name','picture','price,brand,stock','description']}
	 * **/
	@SuppressWarnings("rawtypes")
	public List form(String[] names,Object params,List<Object> htmlSetting){
		ObjectMetadata meta = metaDao.getByName(names[0]);
		List<ObjectField> fields = meta.getFields();
		for(int i = 1; i < names.length; i++){
			for(ObjectField of : fields){
				if(of.getName().equals(names[i])){
					fields = of.getFields();
					break;
				}
			}
		}
		
		return readInputs(fields,params,htmlSetting);
	}
	
	/**
	 * ['description','详情描述','',pro.info.description,'[n]'],
	 * ['picture','图片','',pro.info.picture,'[n]'],
	 * ['price','价格','',pro.info.price,'[n]'],
	 * ['brand','品牌','',pro.info.brand,'[n]'],
	 * ['name','名称','',pro.info.name,'[n]'],
	 * ['stock','库存','',pro.info.stock,'[n]']
	 * **/
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected List readInputs(List<ObjectField> fields,Object params,List htmlSetting){
		List inputs = new ArrayList();
		Map<String,ObjectField> fieldMap = new LinkedMap();
		for(ObjectField of : fields){
			if(FieldType.INNER_COLLECTION.is(of.getType())){
				Object innerVal = MapHelper.readValue(params, of.getName());
				List innerSetting = null;
				if(htmlSetting != null && htmlSetting.size() > 0){
					for(Object o : htmlSetting){
						if(o instanceof Map){
							if(((Map)o).containsKey(of.getName())){
								innerSetting = (List)((Map)o).get(of.getName());
								break;
							}
						}
					}
				}

				List innInputs = readInputs(of.getFields(),innerVal,innerSetting);
				inputs.addAll(innInputs);
				continue;
			}else {
				fieldMap.put(of.getName(),of);
			}
		}
		
		if(fieldMap.size() > 0){
			if(htmlSetting != null && htmlSetting.size() > 0){
				for(Object o:htmlSetting){
					if(o instanceof String){
						String fname = (String)o;
						
						if(fname.contains(",")){
							String[] ns = fname.split(",");
							
							boolean newline = false;
							for(int i = 0; i < ns.length; i++){
								if(i == ns.length-1){
									newline = true;
								}
								inputs.add(readInput(fieldMap.get(ns[i]),params,null,newline));
							}
						}else {
							ObjectField of = fieldMap.get(fname);
							inputs.add(readInput(of,params,null,true));
						}
					}else if(o instanceof List){
						int idx = 1;
						boolean newline = false;
						for(Object oo : (List)o){
							if(idx++ == ((List)o).size()){
								newline = true;
							}
							
							if(oo instanceof String){
								ObjectField of = fieldMap.get((String)oo);
								inputs.add(readInput(of,params,null,newline));
							}else if(oo instanceof Map){
								Map<String,Object> set = (Map<String,Object>)oo;
								for(String key : set.keySet()){
									ObjectField of = fieldMap.get(key);
									inputs.add(readInput(of,params,set.get(key),newline));
								}
							}
						}
					}else if(o instanceof Map){
						Map<String,Object> set = (Map<String,Object>)o;
						for(String key : set.keySet()){
							ObjectField of = fieldMap.get(key);
							inputs.add(readInput(of,params,set.get(key),true));
						}
					}
				}
			}else {
				for(ObjectField of : fieldMap.values()){
					inputs.add(readInput(of,params,null,true));
				}
			}
		}
		
		return inputs;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	List readInput(ObjectField field,Object params,Object inputType,boolean newline){
		if(field == null)return null;
		
		String fieldName = field.getName();
		Object fieldVal = MapHelper.readValue(params, fieldName);
		inputType = readInputType(field,inputType);
		
		List input = new ArrayList();
		input.add(fieldName);
		input.add(field.getLabel());
		input.add(inputType);
		input.add(fieldVal);
		if(newline){
			input.add("[n]");
		}
		
		return input;
	}
	
	Object readInputType(ObjectField field,Object type){
		if(type != null)return type;
		
		if(FieldType.DATE.is(field.getType())){
			type = "date";
		}
		
		if(type == null)type = "";
		
		return type;
	}
	
	public static void main(String[] args) {
		String s = "{info:{name:'乳液',price:200.98,brand:'pola',stock:34},"
		    + "settings:['name','picture',['price',{brand:['select',['pola','迪奥','蓓丽']]},'stock'],{description:'html'}]}";
		
		//s = "settings:[{province:['select',provinces,{change:'refreshCity'}]},{city:['select',cities,{change:'refreshDistrict'}]}]";
		Map<String,Object> params = (Map<String,Object>)ArithmeticExpression.execute(s,null);
		
		s = "[['$bid','','hidden','dddddd'],['$fid','','hidden','2']]+h.Product.info.form([info,settings])";
		Object fs = ArithmeticExpression.execute(s,params);
		System.out.println(fs);
	}
}
