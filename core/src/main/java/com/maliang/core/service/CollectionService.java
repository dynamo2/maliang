package com.maliang.core.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import com.maliang.core.arithmetic.ArithmeticExpression;
import com.maliang.core.dao.CollectionDao;
import com.maliang.core.dao.ObjectMetadataDao;
import com.maliang.core.model.FieldType;
import com.maliang.core.model.ObjectField;
import com.maliang.core.model.ObjectMetadata;
import com.maliang.core.ui.controller.Pager;
import com.maliang.core.util.StringUtil;

public class CollectionService {
	public final static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	public final static DateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public final static DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
	
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
	
	public List<Map<String,Object>> find(Object query){
		Map v = null;
		if(query != null && query instanceof Map){
			v = (Map)query;
		}
		
		return this.find(v);
	}
	
	public Map<String,Object> findOne(Object query){
		Map v = null;
		if(query != null && query instanceof Map){
			v = (Map)query;
		}
		
		List<Map<String,Object>> results = this.find(v);
		if(results != null && results.size() > 0){
			return results.get(0);
		}
		return null;
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
		
		dataMap = this.correctData(dataMap,this.collection);
		this.collectionDao.save(dataMap, this.collection);
		return dataMap;
	}
	
	private Map<String,Object> correctData(Map<String,Object> dataMap,String collName){
		if(dataMap == null)return null;
		
		ObjectMetadata metadata = this.metaDao.getByName(collName);
		return correctData(dataMap,metadata.getFields());
	}
	
	private Map<String,Object> correctData(Map<String,Object> dataMap,List<ObjectField> fields){
		if(dataMap == null)return null;
		
		Map<String,Object> newMap = new HashMap<String,Object>();
		if(!StringUtil.isEmpty((String)dataMap.get("id"))){
			newMap.put("_id",new ObjectId(dataMap.get("id").toString().trim()));
		}
		
		for(ObjectField of : fields){
			String fieldName = of.getName();
			Object fieldValue = dataMap.get(fieldName);
			if(fieldValue == null)continue;
			
			newMap.put(fieldName, correctFieldValue(of,fieldValue));
		}
		return newMap;
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
		
		if(FieldType.INNER_COLLECTION.is(of.getType())){
			if(fieldValue instanceof Map){
				if(((Map) fieldValue).get("id") == null){
					((Map) fieldValue).put("id",new ObjectId().toString());
				}
				
				return correctData((Map<String,Object>)fieldValue,of.getFields());
			}
		}
		
		if(FieldType.LINK_COLLECTION.is(of.getType())){
			if(fieldValue instanceof Map){
				return correctData((Map<String,Object>)fieldValue,of.getLinkedObject());
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
		
		if(FieldType.DATE.is(ftype)){
			try {
				return timestampFormat.parse(value.toString().trim());
			}catch(ParseException e){
				try {
					return dateFormat.parse(value.toString().trim());
				}catch(ParseException ee){
					return null;
				}
			}
		}
		
		return value;
	}
	
	
	
	public static void main(String[] args) {
		String s = "db.User.save({birthday:'1981-4-9', real_name:'www', password:'123456', user:'wmx', id:'', user_grade:'55c8be3f1b970b1ff3fc2f6c'})";
		//s = "db.Product.search({})";
		
		s = "addToParams({uname:'wmx',password:'jjj',user:db.User.get({user:uname}),c1:check([notNull(user),'用户名不存在']),c2:check([user.password=password,'密码错误',['注册新用户','忘记密码']])})";
		//s = "db.User.get({user:'wmx'})";
		Object u = ArithmeticExpression.execute(s, new HashMap());
		System.out.println(u);
	}
	
	@SuppressWarnings("rawtypes")
	public Object invoke(String method,Object value){
		if("get".equals(method)){
			if(value != null && value instanceof Map){
				return this.findOne(value);
			}
			
			String v = value==null?null:value.toString();
			return this.get(v);
		}
		
		if(QUERIES_ALIAS.contains(method)){
			return this.find(value);
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
