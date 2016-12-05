package com.maliang.core.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import com.maliang.core.arithmetic.ArithmeticExpression;
import com.maliang.core.dao.CollectionDao;
import com.maliang.core.dao.ObjectMetadataDao;
import com.maliang.core.ui.controller.Pager;
import com.maliang.core.util.StringUtil;
import com.maliang.core.util.Utils;

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
	
	public Map<String,Object> get(String id,String innerName){
		if(id == null || id.trim().isEmpty()){
			return null;
		}
		
		id = id.trim();
		return this.collectionDao.getInnerObject(this.collection, innerName, id);
	}
	
	//command $set
	public Object dbSet(Map<String,Object> query,Map<String,Object> set){
		this.collectionDao.dbSet(query,set,this.collection,true);
		return null;
	}
	
	//command $set
	public Object dbSetOne(Map<String,Object> query,Map<String,Object> set){
		this.collectionDao.dbSet(query,set,this.collection,false);
		return null;
	}
	
	public Map<String,Object> innerObjectById(Object query){
		Map v = null;
		if(query != null && query instanceof Map){
			v = (Map)query;
		}
		
		return this.collectionDao.innerObjectById(v, this.collection);
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
	
	public List<Map<String,Object>> aggregate(List<Map<String,Object>> query){
		return this.collectionDao.aggregateByMap(query, this.collection);
	}
	
	public Map<String,Object> aggregateOne(List<Map<String,Object>> query){
		return this.collectionDao.aggregateOne(query, this.collection);
	}
	
	public Map<String,Object> save(Object obj){
		if(obj == null || !(obj instanceof Map))return null;
		
		Map<String,Object> dataMap = (Map<String,Object>)obj;
		//if(!StringUtil.isEmpty((String)dataMap.get("id"))){ 
		if(hasId(dataMap)){// Update
			//dataMap = this.correctData(dataMap,this.collection,false);
			dataMap = this.collectionDao.correctData(dataMap, this.collection,false,false);
			return this.collectionDao.updateBySet(dataMap, this.collection);
		}else { // Save
			//dataMap = this.correctData(dataMap,this.collection,true);
			
			dataMap = this.collectionDao.correctData(dataMap, this.collection,true,false);
			return this.collectionDao.save(dataMap, this.collection);
		}
	}
	
	/**
	 * 检索该对象中是否有有效ID值，支持递归检索
	 * ***/
	private boolean hasId(Map<String,Object> dataMap){
		try {
			Object id = dataMap.get("id");
			if(id == null){
				id = dataMap.get("_id");
			}
			
			new ObjectId(id.toString());
			return true;
		}catch(Exception e){}
		
		return hasId(dataMap.values());
	}
	
	private boolean hasId(Object[] os){
		for(Object o : Utils.toArray(os)){
			if(hasId(o)){
				return true;
			}
		}
		
		return false;
	}
	
	private boolean hasId(Object o){
		if(o instanceof Map){
			return hasId((Map<String,Object>)o);
		}
		if(Utils.isArray(o)){
			return hasId(Utils.toArray(o));
		}
		return false;
	}
	
	public void removeAll(){
		this.collectionDao.removeAll(this.collection);
	}
	
	public int remove(String oid){
		return this.collectionDao.remove(oid,this.collection);
	}
	
	public int remove(Map<String,Object> query){
		return this.collectionDao.remove(query,this.collection);
	}
	
	private Map<String,Object> convert(Map<String,Object> dataMap){
		return convert(dataMap,false);
	}
	
	private Map<String,Object> deepConvert(Map<String,Object> dataMap){
		return convert(dataMap,true);
	}
	
	private Map<String,Object> convert(Map<String,Object> dataMap,boolean isDeep){
		if(Utils.isEmpty(dataMap)){
			return dataMap;
		}
		
		return this.collectionDao.correctData(dataMap, this.collection,false,isDeep);
	}
	
	public static void main(String[] args) {
		String s = "db.User.save({birthday:'1981-4-9', real_name:'www', password:'123456', user:'wmx', id:'', user_grade:'55c8be3f1b970b1ff3fc2f6c'})";
		//s = "db.Product.search({})";
		
		s = "addToParams({uname:'wmx',password:'jjj',user:db.User.get({user:uname}),c1:check([notNull(user),'用户名不存在']),c2:check([user.password=password,'密码错误',['注册新用户','忘记密码']])})";
		//s = "db.User.get({user:'wmx'})";
		Object u = ArithmeticExpression.execute(s, new HashMap());
		System.out.println(u);
	}
	
	private boolean isInnerObject(){
		return this.collection.contains(".");
	}
	
	
	@SuppressWarnings("rawtypes")
	public Object invoke(String method,Object value){
		boolean isInner = isInnerObject();
		String innerName = null;
		if(isInner){
			int idx = this.collection.indexOf(".");
			innerName = this.collection.substring(idx+1);
			//innerName = this.collection;
			this.collection = this.collection.substring(0,idx);
		}
		
		if("get".equals(method)){
			if(value != null && value instanceof Map){
				return this.findOne(value);
			}
			
			String v = value==null?null:value.toString();
			if(isInner){
				//Map query = this.collectionDao.buildInnerGetMap(innerName, v);
				//return this.innerObjectById(query);
				return this.get(v,innerName);
			}else {
				return this.get(v);
			}
		}
		
		if("convert".equals(method)){
			if(value != null && value instanceof Map){
				return this.convert((Map<String,Object>)value);
			}
			
			return null;
		}
		
		if("deepConvert".equals(method)){
			if(value != null && value instanceof Map){
				return this.deepConvert((Map<String,Object>)value);
			}
			
			return null;
		}
		
		if("innerObjectById".equals(method)){
			Object obj = this.innerObjectById(value);
			return obj;
		}
		
		if("delete".equals(method) || "remove".equals(method) || "del".equals(method)){
			if(value instanceof Map){
				return this.remove((Map<String,Object>)value);
			}
			
			String v = value==null?null:value.toString();
			if(isInner){
				Map query = this.collectionDao.buildInnerGetMap(innerName, v);
				
				Map delMap = new HashMap();
				delMap.put(innerName+".$", null);
				
				return this.dbSet(query,delMap);
			}
			
			return this.remove(v);
		}
		
		if("set".equals(method)){
			if(value != null && value instanceof List && ((List)value).size() == 2){
				Object query = ((List)value).get(0);
				if(query == null || !(query instanceof Map))return null;
				
				Object set = ((List)value).get(1);
				if(set == null || !(set instanceof Map))return null;
				
				return this.dbSet((Map<String,Object>)query,(Map<String,Object>)set);
			}
			return null;
		}
		
		if("setOne".equals(method)){
			if(value != null && value instanceof List && ((List)value).size() == 2){
				Object query = ((List)value).get(0);
				if(query == null || !(query instanceof Map))return null;
				
				Object set = ((List)value).get(1);
				if(set == null || !(set instanceof Map))return null;
				
				return this.dbSetOne((Map<String,Object>)query,(Map<String,Object>)set);
			}
			return null;
		}
		
		if("removeAll".equals(method)){
			this.removeAll();
			return null;
		}
		
		if("save".equals(method)){
			return this.save(value);
		}
		
		if("aggregate".equals(method)){
			return this.aggregate((List<Map<String,Object>>)value);
		}
		
		if("aggregateOne".equals(method)){
			return this.aggregateOne((List<Map<String,Object>>)value);
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
