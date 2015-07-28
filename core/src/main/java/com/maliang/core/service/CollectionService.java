package com.maliang.core.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.maliang.core.arithmetic.ArithmeticExpression;
import com.maliang.core.dao.CollectionDao;
import com.maliang.core.dao.ObjectMetadataDao;
import com.maliang.core.model.FieldType;
import com.maliang.core.model.ObjectField;
import com.maliang.core.model.ObjectMetadata;
import com.maliang.core.ui.controller.Pager;

public class CollectionService {
	/**
	 * DB 查询方法的别名
	 * **/
	private static List<String> QUERIES_ALIAS = new ArrayList<String>();  
	private String collection;
	private CollectionDao collectionDao = new CollectionDao();
	protected ObjectMetadataDao metaDao = new ObjectMetadataDao();
	
	{
		QUERIES_ALIAS.add("query");
		QUERIES_ALIAS.add("find");
		QUERIES_ALIAS.add("search");
		QUERIES_ALIAS.add("select");
	}
	
	public CollectionService(String coll) {
		this.collection = coll;
	}
	
	public Map<String,Object> get(String id){
		if(id == null || id.trim().isEmpty()){
			return null;
		}
		
		id = id.trim();
		return this.collectionDao.getByID(id, this.collection);
	}
	
	public List<Map<String,Object>> find(Map query){
		return this.collectionDao.findByMap(query, this.collection);
	}
	
	public Map<String,Object> find(Map<String,Object> query,Map<String,Object> sort,Pager page){
		List<Map<String,Object>> datas = this.collectionDao.findByMap(query,sort,page,this.collection);
		
		Map<String,Object> pageMap = new HashMap<String,Object>();
		pageMap.put("currentPage",page.getCurPage());
		pageMap.put("totalRows",page.getTotalRow());
		pageMap.put("pageSize",page.getPageSize());
		pageMap.put("datas",datas);
		
		return pageMap;
		//return this.collectionDao.findByMap(query, this.collection);
	}
	
	public Map<String,Object> save(Object obj){
		if(obj == null || !(obj instanceof Map))return null;
		
		Map<String,Object> dataMap = (Map<String,Object>)obj;
		
		this.correctData(dataMap,this.collection);
		//System.out.println("dataMap: " + ((List)dataMap.get("values")).get(0).getClass());
		this.collectionDao.save(dataMap, this.collection);
		return dataMap;
	}
	
	private void correctData(Map<String,Object> dataMap,String collName){
		if(dataMap == null)return;
		
		ObjectMetadata metadata = this.metaDao.getByName(collName);
		for(ObjectField of : metadata.getFields()){
			String fieldName = of.getName();
			Object fieldValue = dataMap.get(fieldName);
			if(fieldValue == null)continue;
			
			dataMap.put(fieldName, correctFieldValue(of,fieldValue));
		}
	}
	
	private Object correctFieldValue(ObjectField of,Object fieldValue){
		if(FieldType.ARRAY.is(of.getType())){
			if(fieldValue instanceof List){
				List<Object> result = new ArrayList<Object>();
				for(Object o : (List<Object>)fieldValue){
					of.setType(of.getElementType());
					
					result.add(correctFieldValue(of,o));
				}
				
				return result;
			}else {
				return null;
			}
		}
		
		if(FieldType.LINK_COLLECTION.is(of.getType()) 
				|| FieldType.INNER_COLLECTION.is(of.getType())){
			if(fieldValue instanceof Map){
				Map<String,Object> result = (Map<String,Object>)fieldValue;
				correctData(result,of.getLinkedObject());
				
				return result;
			}else {
				return null;
			}
		}

		return correctFieldValue(of.getType(),fieldValue);
	}
	
	private Object correctFieldValue(int ftype,Object value){
		if(FieldType.DOUBLE.is(ftype)){
			try {
				return Double.valueOf(value.toString().trim());
			}catch(Exception e){
				return null;
			}
		}
		
		if(FieldType.INT.is(ftype)){
			try {
				return Integer.valueOf(value.toString().trim());
			}catch(Exception e){
				return null;
			}
		}
		
		return value;
	}
	
	public static void main(String[] args) {
		String page = "{brands:db.Brand.search(),page:{start:1},pros:db.Product.page({page:page})}";
		
		Object ov = ArithmeticExpression.execute(page, null);
		System.out.println(ov);
	}
	
	public Object invoke(String method,Object value){
		if("get".equals(method)){
			String v = value==null?null:value.toString();
			return this.get(v);
		}
		
		if(QUERIES_ALIAS.contains(method)){
			Map v = null;
			if(value != null && value instanceof Map){
				v = (Map)value;
			}
			return this.find(v);
		}
		
		if("page".equals(method)){
			Map v = null;
			Pager page = new Pager();
			Map<String,Object> sort = null;
			Map<String,Object> query = null;
			if(value != null && value instanceof Map){
				v = (Map)value;

				page.setStart((Integer)MapHelper.readValue(v,"page.start",0));
				page.setPageSize((Integer)MapHelper.readValue(v,"page.pageSize",0));
				
				query = (Map<String,Object>)MapHelper.readValue(v,"query");
				sort = (Map<String,Object>)MapHelper.readValue(v,"sort");
			}

			return this.find(query,sort,page);
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
