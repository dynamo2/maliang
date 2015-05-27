package com.maliang.core.service;

import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import com.maliang.core.dao.CollectionDao;
import com.maliang.core.dao.ObjectMetadataDao;
import com.maliang.core.model.FieldType;
import com.maliang.core.model.ObjectField;
import com.maliang.core.model.ObjectMetadata;

public class BusinessService {
	private String collection;
	private CollectionDao collectionDao = new CollectionDao();
	protected ObjectMetadataDao metaDao = new ObjectMetadataDao();
	
	public BusinessService(String coll) {
		this.collection = coll;
	}
	
	public Map<String,Object> get(String id){
		if(id == null || id.trim().isEmpty()){
			return null;
		}
		
		id = id.trim();
		return this.collectionDao.getByID(id, this.collection);
	}
	
	public List<Map<String,Object>> query(){
		return this.collectionDao.find(null, this.collection);
	}
	
	public Map<String,Object> save(Object obj){
		if(obj == null || !(obj instanceof Map))return null;
		
		Map<String,Object> dataMap = (Map<String,Object>)obj;
		System.out.println("BusinessService save : " + dataMap);
		
		this.correctData(dataMap);
		this.collectionDao.save(dataMap, this.collection);
		return dataMap;
	}
	
	private void correctData(Map<String,Object> dataMap){
		if(dataMap == null)return;
		
		ObjectMetadata metadata = this.metaDao.getByName(this.collection);
		for(ObjectField of : metadata.getFields()){
			String fieldName = of.getName();
			Object fieldValue = dataMap.get(fieldName);
			if(fieldValue == null)continue;
			
			dataMap.put(fieldName, correctFieldValue(of,fieldValue));
		}
	}
	
	private Object correctFieldValue(ObjectField of,Object fieldValue){
		if(FieldType.DOUBLE.is(of.getType())){
			try {
				return Double.valueOf(fieldValue.toString().trim());
			}catch(Exception e){
				return null;
			}
		}
		
		if(FieldType.INT.is(of.getType())){
			try {
				return Integer.valueOf(fieldValue.toString().trim());
			}catch(Exception e){
				return null;
			}
		}
		
		return fieldValue;
	}
	
	public static void main(String[] args) {
		BusinessService db = new BusinessService("ProductType");
		
		System.out.println("query = "+db.invoke("query", null));
	}
	
	public Object invoke(String method,Object value){
		if("get".equals(method)){
			String v = value==null?null:value.toString();
			return this.get(v);
		}
		
		if("query".equals(method) || "search".equals(method)){
			return this.query();
		}
		
		if("save".equals(method)){
			return this.save(value);
		}
		
		/*
		try {
			Class vc = value == null?null:value.getClass();
			Method md = DBService.class.getMethod(method,vc);
			return md.invoke(this,value);
		} catch (Exception e) {}
		*/
		
		return null;
	}
}
