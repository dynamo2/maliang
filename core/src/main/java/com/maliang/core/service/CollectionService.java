package com.maliang.core.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import com.maliang.core.arithmetic.ArithmeticExpression;
import com.maliang.core.dao.BusinessDao;
import com.maliang.core.dao.CollectionDao;
import com.maliang.core.dao.ObjectMetadataDao;
import com.maliang.core.model.Business;
import com.maliang.core.model.Workflow;
import com.maliang.core.ui.controller.Pager;
import com.maliang.core.util.StringUtil;
import com.maliang.core.util.Utils;

public class CollectionService {
	public final static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	public final static DateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public final static DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
	
	/**
	 * DB 鏌ヨ鏂规硶鐨勫埆鍚�
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
	public Object update(Map<String,Object> query,Map<String,Object> set){
		this.collectionDao.update(query,set,this.collection);
		return null;
	}
	
	//command $set
		public int updateAll(Map<String,Object> update){
			return this.collectionDao.updateAll(update,this.collection);
		}
	
	//command $set
//	public Object dbSetOne(Map<String,Object> query,Map<String,Object> set){
//		this.collectionDao.update(query,set,this.collection,false);
//		return null;
//	}
	
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
	
	public List<Map<String,Object>> find(Object query,String innerName){
		Map mquery = null;
		if(query != null && query instanceof Map){
			mquery = (Map<String,Object>)query;
		}
		return this.collectionDao.findByMap(null,mquery,null,null,this.collection,innerName);
	}
	
	public List<Map<String,Object>> find(Map query){
		return this.collectionDao.findByMap(query, this.collection);
	}
	
	public Map<String,Object> find(Map<String,Object> query,Map<String,Object> sort,Pager page){
		List<Map<String,Object>> datas = this.collectionDao.findByMap(query,sort,page,this.collection);
		
		return pageMap(page,datas);
	}
	
	public Object page(Map<String,Object> match,Map<String,Object> query,Map<String,Object> sort,Pager page,String innerName){
		List<Map<String,Object>> datas =  this.collectionDao.findByMap(match,query,sort,page,this.collection,innerName);
		
		return pageMap(page,datas);
	}
	
	private Map<String,Object> pageMap(Pager page,List<Map<String,Object>> datas){
		Map<String,Object> pageMap = new HashMap<String,Object>();
		pageMap.put("currentPage",page.getCurPage());
		pageMap.put("totalRows",page.getTotalRow());
		pageMap.put("pageSize",page.getPageSize());
		pageMap.put("totalPage",page.getTotalPage());
		pageMap.put("datas",datas);
		
		return pageMap;
	}
	
	public List<Map<String,Object>> aggregate(List<Map<String,Object>> query){
		return this.collectionDao.aggregateByMap(query, this.collection);
	}
	
	public Map<String,Object> aggregateOne(List<Map<String,Object>> query){
		return this.collectionDao.aggregateOne(query, this.collection);
	}
	
	
	//保存
	public Map<String,Object> save(Object obj,boolean updateAllArray){
		if(obj == null || !(obj instanceof Map))return null;
		
		Map<String,Object> dataMap = (Map<String,Object>)obj;
		if(hasId(dataMap)){// Update
			/*
			dataMap = this.collectionDao.formatData(dataMap, this.collection,false,false);
			//dataMap = this.collectionDao.toDBModel(dataMap, this.collection);
			*/
			
			System.out.println("------------- update formatData data: " + dataMap);
			
			return this.collectionDao.updateBySet(dataMap, this.collection,updateAllArray);
		}else { // Save
			/*
			dataMap = this.collectionDao.formatData(dataMap, this.collection,true,false);
			//dataMap = this.collectionDao.toDBModel(dataMap, this.collection);
			return this.collectionDao.save(dataMap, this.collection);
			*/
			
			return this.collectionDao.insert(dataMap, this.collection);
			
		}
	}
	
	/**
	 * 妫�绱㈣瀵硅薄涓槸鍚︽湁鏈夋晥ID鍊硷紝鏀寔閫掑綊妫�绱�
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
	
	public int deleteIncludeChildren(String oid){
		this.collectionDao.deleteIncludeChildren(oid,this.collection);
		return 1;
	}
	
	public int remove(Map<String,Object> query){
		return this.collectionDao.remove(query,this.collection);
	}
	
	public Object resetPath(String oid,boolean resetChildren){
		this.collectionDao.resetPath(oid, this.collection, false);
		return null;
	}
	
	public Object resetPath(){
		this.collectionDao.resetPath(this.collection);
		return null;
	}
	
	private Map<String,Object> format(Map<String,Object> dataMap){
		return format(dataMap,false);
	}
	
	private Map<String,Object> deepConvert(Map<String,Object> dataMap){
		return format(dataMap,true);
	}
	
	private Map<String,Object> format(Map<String,Object> dataMap,boolean loadLinked){
		if(Utils.isEmpty(dataMap)){
			return dataMap;
		}
		
		//return this.collectionDao.formatData(dataMap, this.collection,false,isDeep);
		
		this.collectionDao.formatData(dataMap, this.collection);
		if(loadLinked) {
			this.collectionDao.readLinkedCollection(dataMap, this.collection);
		}
		
		return dataMap;
	}
	
	public static void main(String[] args) {
		String s = "db.User.save({birthday:'1981-4-9', real_name:'www', password:'123456', user:'wmx', id:'', user_grade:'55c8be3f1b970b1ff3fc2f6c'})";
		//s = "db.Product.search({})";
		
		s = "addToParams({uname:'wmx',password:'jjj',user:db.User.get({user:uname}),c1:check([notNull(user),'鐢ㄦ埛鍚嶄笉瀛樺湪']),c2:check([user.password=password,'瀵嗙爜閿欒',['娉ㄥ唽鏂扮敤鎴�','蹇樿瀵嗙爜']])})";
		//s = "db.User.get({user:'wmx'})";
		Object u = ArithmeticExpression.execute(s, new HashMap());
		System.out.println(u);
	}
	
	private boolean isInnerObject(){
		return this.collection.contains(".");
	}
	
	private int deleteOneArrayInner(String innerName,String id){
		return this.collectionDao.deleteArrayDocument(this.collection, innerName, id);
	}
	
	private Map<String,Object> newMap(String key,Object val){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put(key,val);
		return map;
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
		
		if("format".equals(method)){
			System.out.println("------- covert "+(Map<String,Object>)value);
			if(value != null && value instanceof Map){
				return this.format((Map<String,Object>)value);
			}
			
			return null;
		}
		
		if("formatWithLinked".equals(method)){
			if(value != null && value instanceof Map){
				return this.deepConvert((Map<String,Object>)value);
			}
			
			return null;
		}
		
		if("innerObjectById".equals(method)){
			Object obj = this.innerObjectById(value);
			return obj;
		}
		
		if("deleteIncludeChildren".equals(method)){
			String v = value==null?null:value.toString();
			return this.deleteIncludeChildren(v);
		}
		
		if("delete".equals(method) || "remove".equals(method) || "del".equals(method)){
			if(value instanceof Map){
				return this.remove((Map<String,Object>)value);
			}
			
			if(isInner){
				if(Utils.isArray(value)){
					for(Object ov : Utils.toArray(value)){
						String id = ov==null?null:ov.toString();
						deleteOneArrayInner(innerName,id);
					}
				}else {
					String id = value==null?null:value.toString();
					deleteOneArrayInner(innerName,id);
				}
				return null;
			}
			
			String v = value==null?null:value.toString();
			return this.remove(v);
		}
		
		if("update".equals(method)){
			if(value != null && value instanceof List && ((List)value).size() == 2){
				Object query = ((List)value).get(0);
				if(query == null || !(query instanceof Map))return null;
				
				Object set = ((List)value).get(1);
				if(set == null || !(set instanceof Map))return null;
				
				return this.update((Map<String,Object>)query,(Map<String,Object>)set);
			}
			return null;
		}
		
		if("updateAll".equals(method)){
			if(value instanceof Map){
				return this.updateAll((Map<String,Object>)value);
			}
			
			return 0;
		}
		
//		if("updateOne".equals(method)){
//			if(value != null && value instanceof List && ((List)value).size() == 2){
//				Object query = ((List)value).get(0);
//				if(query == null || !(query instanceof Map))return null;
//				
//				Object set = ((List)value).get(1);
//				if(set == null || !(set instanceof Map))return null;
//				
//				return this.dbSetOne((Map<String,Object>)query,(Map<String,Object>)set);
//			}
//			return null;
//		}
		
		if("removeAll".equals(method)){
			this.removeAll();
			return null;
		}
		
		if("save".equals(method)){
			if(isInner){
				Map mval = new HashMap();
				mval.put(innerName, value);
				
				this.save(mval,false);
				return value;
			}
			
			return this.save(value,false);
		}
		
		if("saveAllArray".equals(method)){
			if(isInner){
				Map mval = new HashMap();
				mval.put(innerName, value);
				
				this.save(mval,true);
				return value;
			}
			
			return this.save(value,true);
		}
		
		if("aggregate".equals(method)){
			return this.aggregate((List<Map<String,Object>>)value);
		}
		
		if("aggregateOne".equals(method)){
			return this.aggregateOne((List<Map<String,Object>>)value);
		}

		if(QUERIES_ALIAS.contains(method)){
			if(StringUtil.isEmpty(innerName)){
				return this.find(value);
			}
			return this.find(value,innerName);
		}

		if("page".equals(method)){
			Map v = null;
			Pager page = new Pager();
			Map<String,Object> sort = null;
			Map<String,Object> query = null;
			Map<String,Object> match = null;
			if(value != null && value instanceof Map){
				v = (Map)value;

				page.setPageSize(MapHelper.readValue(v,"page.pageSize",Pager.PAGE_SIZE));
				page.setCurPage(MapHelper.readValue(v,"page.page",1));

				match = (Map<String,Object>)MapHelper.readValue(v,"match");
				query = (Map<String,Object>)MapHelper.readValue(v,"query");
				sort = (Map<String,Object>)MapHelper.readValue(v,"sort");
			}

			if(StringUtil.isEmpty(innerName)){
				return this.find(query,sort,page);
			}
			
			return this.page(match,query,sort,page,innerName);
		}
		
		if("resetPath".equals(method)){
			if(Utils.isEmpty(value)) {
				return this.resetPath();
			}
			
			String oid = null;
			boolean resetChildren = false;
			if(value instanceof Map){
				Object val = MapHelper.readValue(value,"id");
				if(Utils.isEmpty(val)) {
					return this.resetPath();
				}
				oid = val.toString();
				
				val = MapHelper.readValue(value,"resetChildren");
				resetChildren = Utils.toBoolean(val);
			}else {
				oid = value.toString();
			}
			
			if(Utils.isEmpty(oid)) {
				return this.resetPath();
			}
			
			return this.resetPath(oid,resetChildren);
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
