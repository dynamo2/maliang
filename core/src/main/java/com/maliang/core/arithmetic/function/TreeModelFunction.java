package com.maliang.core.arithmetic.function;

import java.util.Map;

import com.maliang.core.dao.CollectionDao;
import com.maliang.core.service.MapHelper;
import com.mongodb.BasicDBObject;

class TreeModelFunction {
	public static Object parent(Function function,Map<String,Object> params){
		Object value = function.getKeyValue();
		if(value == null){
			return null;
		}
		
		return MapHelper.readValue(value,"_parent_");
	}
	
	public static Object parents(Function function,Map<String,Object> params){
		Object value = function.getKeyValue();
		if(value == null){
			return null;
		}
		
		return MapHelper.readValue(value,"_path_");
	}
	
	public static Object children(Function function,Map<String,Object> params){
		Object value = function.getKeyValue();
		if(value == null){
			return null;
		}
		
		Object id = MapHelper.readValue(value,"id");
		String collName = (String)MapHelper.readValue(value,"_meta.type");
		
		CollectionDao dao = new CollectionDao();
		BasicDBObject query = new BasicDBObject();
		query.put("_parent_",id.toString());
		
		Object list = dao.find(query, collName);
		
		System.out.println("children query : " + query);
		System.out.println("children collName : " + collName);
		System.out.println("children list : " + list);
		
		return list;
	}
	
	public static Object allChildren(Function function,Map<String,Object> params){
		Object value = function.getKeyValue();
		if(value == null){
			return null;
		}
		
		Object id = MapHelper.readValue(value,"id");
		String collName = (String)MapHelper.readValue(value,"_meta.type");
		
		CollectionDao dao = new CollectionDao();
		BasicDBObject query = new BasicDBObject();
		query.put("_path_",id.toString());
		
		Object list = dao.find(query, collName);
		
		System.out.println("allChildren query : " + query);
		System.out.println("allChildren collName : " + collName);
		System.out.println("allChildren list : " + list);
		
		return list;
	}
}
