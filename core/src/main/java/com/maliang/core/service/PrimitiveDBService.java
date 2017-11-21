package com.maliang.core.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.maliang.core.dao.PrimitiveDao;
import com.maliang.core.util.StringUtil;
import com.maliang.core.util.Utils;
import com.mongodb.WriteResult;

public class PrimitiveDBService {
	private static PrimitiveDao pDao = new PrimitiveDao();
	
	
	private static Object get(Object val,String collection,boolean isInner){
		if(val == null)val = new HashMap();
		if(val instanceof Integer){
			return findOne(new HashMap(),collection,(Integer)val);
		}
		if(val instanceof Map){
			return findOne((Map)val,collection,0);
		}
		
		String innerName = null;
		if(isInner){
			int idx = collection.indexOf(".");
			innerName = collection.substring(idx+1);
			collection = collection.substring(0,idx);
		}

		if(isInner){
			return pDao.getInnerObject(collection, innerName, val);
		}
		return pDao.getByID(val, collection);
	}
	
	private static Object save(Object val,String collection,boolean isInner){
		return pDao.save(val,collection);
	}
	
	private static Object findRandom(Object val,String collection,boolean isInner){
		int size = -1;
		if(val != null && val instanceof Number){
			size = ((Number)val).intValue();
		}
		
		return pDao.find(null, size,true, collection);
	}
	
	private static WriteResult remove(Object val,String collection,boolean isInner){
		return pDao.remove(val,collection);
	}
	
	private static Object last(Object val, String collection,boolean isInner){
		List vals = find(val,collection,isInner);
		if(!Utils.isEmpty(vals)){
			return vals.get(vals.size()-1);
		}
		return null;
	}
	
	private static List find(Object val, String collection,boolean isInner){
		if(val == null)val = new HashMap();
		if(val instanceof Map){
			return pDao.find((Map)val,collection);
		}
		
		if(val instanceof String){
			 Object result = get((String)val,collection,isInner);
			 List rs = new ArrayList();
			 rs.add(result);
			 return rs;
		}
		
		if(val instanceof Number){
			return pDao.find(null, ((Number)val).intValue(),false, collection);
		}
		
		return null;
	}
	
	private static List aggregate(Object val, String collection){
		if(Utils.isEmpty(val)){
			return emptyResult();
		}
		
		if(val instanceof Map){
			Map nv = (Map)val;
			val = new ArrayList();
			((List)val).add(nv);
		}
		
		if(!Utils.isArray(val)){
			return emptyResult();
		}
		
		return pDao.aggregate(Utils.toList(val), collection);
	}
	
	private static List emptyResult(){
		return new ArrayList();
	}
	
	private static String use(Object val){
		if(val == null){
			return "use default db [" + pDao.defaultDB()+"]";
		}
		
		pDao.use(val.toString());
		return "use db ["+val+"] ok!";
	}
	
	private static String showDB(){
		return pDao.showDB();
	}
	
	private static Collection showAll(){
		return pDao.showAll();
	}
	
	private static Object findOne(Map query,String collection,int index){
		return pDao.findOne(query, collection,index);
	}
	
	private static boolean isInnerObject(String collection){
		return !StringUtil.isEmpty(collection) && collection.contains(".");
	}
	
	@SuppressWarnings("rawtypes")
	public static Object invoke(String method,String collection,Object value){
		try {
			boolean isInner = isInnerObject(collection);
			
			long bt = System.currentTimeMillis();
			if("get".equals(method)){
				return get(value, collection,isInner);
			}
			
			if("find".equals(method)){
				return find(value, collection,isInner);
			}
			
			if("last".equals(method)){
				return last(value, collection,isInner);
			}
			
			if("findRandom".equals(method)){
				return findRandom(value, collection,isInner);
			}
			
			if("aggregate".equals(method)){
				Object result = aggregate(value,collection);
				
				long at = System.currentTimeMillis();
				System.out.println("用时：" + (at-bt) + " 毫秒");
				
				return result;
			}
			
			if("save".equals(method)){
				return save(value, collection,isInner);
			}
			
			if("update".equals(method)){
				return save(value, collection,isInner);
			}
			
			if("remove".equals(method)){
				return remove(value, collection,isInner);
			}
			
			if("use".equals(method)){
				return use(value);
			}
			
			if("showAll".equals(method)){
				return showAll();
			}
			
			if("showDB".equals(method)){
				return showDB();
			}
		}catch(Exception e){
			return e.getMessage();
			//return Arrays.asList(e.getStackTrace());
		}
		
		
		/*
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
				
				this.save(mval);
				return value;
			}
			
			return this.save(value);
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
			
			System.out.println("-------- page sort : " + sort);

			if(StringUtil.isEmpty(innerName)){
				return this.find(query,sort,page);
			}
			
			return this.page(match,query,sort,page,innerName);
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
