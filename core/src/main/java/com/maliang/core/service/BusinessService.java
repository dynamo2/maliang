package com.maliang.core.service;

import java.util.List;
import java.util.Map;

import com.maliang.core.dao.BusinessDao;

public class BusinessService {
	private String collection;
	private List<Map<String,Object>> collections = null;
	private BusinessDao businessDao = new BusinessDao();
	
	public BusinessService(String coll) {
		this.collection = coll;
	}
	
	public Map<String,Object> get(String id){
		if(id == null || id.trim().isEmpty()){
			return null;
		}
		
		id = id.trim();
		return this.businessDao.getByID(id, this.collection);
	}
	
	public List<Map<String,Object>> query(){
		return this.businessDao.find(null, this.collection);
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
