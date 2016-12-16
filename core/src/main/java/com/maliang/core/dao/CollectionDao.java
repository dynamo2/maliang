package com.maliang.core.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.maliang.core.arithmetic.AE;
import com.maliang.core.arithmetic.ArithmeticExpression;
import com.maliang.core.model.ObjectMetadata;
import com.maliang.core.model.Trigger;
import com.maliang.core.model.TriggerAction;
import com.maliang.core.service.MapHelper;
import com.maliang.core.ui.controller.Pager;
import com.maliang.core.util.Utils;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

public class CollectionDao extends BasicDao {
	public static void main(String[] args) {

		String str = "db.Region.aggregateOne([{$match:{province.name:'浙江'}},{ $unwind :'$province.cities'},"
				+ "{$group:{_id:{$cond:{if:{$eq:['$province.cities.name','绍兴']},then:{ $ifNull:[ '$province.cities.districts',[]]},else:[]}}}},"
				+ "{$redact:{$cond:{if:{$gt:[{$size:'$_id'},0]},then:'$$DESCEND',else:'$$PRUNE'}}}])";
		
		str = "db.Warehouse.aggregateOne([{$unwind :'$stores'},{$group:{_id:'$stores.product',totalStore:{$sum:'$stores.num'}}},{$match:{_id:'574c02d87a779392fcea0c9b'}}])";
		
		str = "db.Warehouse.aggregate([{$unwind :'$stores'},{$match:{stores.product:{$in:['574c014f7a779392fcea0c93','574c01f07a779392fcea0c95']}}}])";
		//str = "db.Cart.items.get('57517b1d8f77ee4257fe9f40')";
		//str = "db.Cart.set([{items:{id:'57517b1d8f77ee4257fe9f40'}},{items.$:null}])";
		
		
		//BasicDBObject query = new BasicDBObject("Cart.items._id",new ObjectId("57517b1d8f77ee4257fe9f40"));
		//new BasicDBObject("$set", set));
		
		
		//str = "db.Cart.removeAll()";
		
		str = "db.Project.dailyProgressNotes.delete('576753b98f77751c91c432ce')";
		
		str = "db.Task.search()";
		
		CollectionDao dao = new CollectionDao();
		Map map = new HashMap();
		map.put("parentTask","576753b98f77751c91c432ce");
		//DBCursor dc = dao.getDBCollection("Task").find(new BasicDBObject(map));
		DBCursor dc = dao.getDBCollection("Task").find(null);
		while(dc.hasNext()){
			System.out.println("***************************");
			System.out.println(dc.next());
		}
		
		//Object v = AE.execute(str);
		//System.out.println(v);
	}

	// public

	// For test
	public BasicDBObject executeDBObject(String str) {
		Map<String, Object> map = (Map<String, Object>) ArithmeticExpression
				.execute(str, null);
		return this.build(this.buildDBQueryMap(map, null));
	}

	

	public int update(Map qMap, Map sMap, String collName) {
		BasicDBObject query = null;
		if(qMap == null){
			query = new BasicDBObject();
		}else {
			query = this.build(this.buildDBQueryMap(qMap, null)); 
		}
		BasicDBObject set = this.build(this.buildDBQueryMap(sMap, null));

		WriteResult result  = this.getDBCollection(collName).updateMulti(query,
				new BasicDBObject("$set", set));
		
		return result.getN();
	}

	public int updateAll(Map value, String collName) {
		return this.update(null, value, collName);
	}

	public Map<String, Object> updateBySet(Map value, String collName) {
		value = this.toDBModel(value, collName);
		
		System.out.println("update value : " + value);
		/**
		 * 执行触发器
		 * **/
		this.trigger(value, collName);
		
		System.out.println("after trigger value : " + value);
		
		String id = (String) value.remove("id");
		BasicDBObject query = this.getObjectId(id);

		ObjectMetadata meta = this.metaDao.getByName(collName);
		List<Map<String, BasicDBObject>> updates = new ArrayList<Map<String, BasicDBObject>>();
		Map<String, Object> daoMap = buildUpdates(meta.getFields(), value,
				null, updates, query);
		updates.add(buildSetUpdateMap(query, daoMap));

		DBCollection db = this.getDBCollection(collName);
		DBObject result = null;
		List<Map<Object,Object>> resultValueMap = new ArrayList<Map<Object,Object>>();
		for (Map<String, BasicDBObject> um : updates) {
			if (um != null) {
				result = db.findAndModify(um.get("query"), null, null, false,
						um.get("update"), true, false);
			}
		}

		value.put("id", id);

		return value;
	}
	
	public void trigger(Map<String,Object> value, String collName) {
		ObjectMetadata metadata = this.metaDao.getByName(collName);
		List<Trigger> triggers = metadata.getTriggers();
		
		if(Utils.isEmpty(triggers))return;
		
		Map<String,Object> dbDataMap = this.getByID((String)value.get("id"), collName);
		Map<String,Object> whenMap = new HashMap<String,Object>();
		triggerParams(whenMap,dbDataMap,value);
		
		boolean newUpdate = true;
		while(newUpdate && !Utils.isEmpty(triggers)){
			List<Trigger> next = new ArrayList<Trigger>();
			newUpdate = false;
			
			for(Trigger trigger : triggers){
				Object when = AE.execute(trigger.getWhen(),whenMap);
				
				if(when instanceof Boolean && (Boolean)when){
					List<TriggerAction> actions = trigger.getActions();
					
					for(TriggerAction ac:actions){
						if(value.get(ac.getField()) != null){
							continue;
						}
						
						Object val = AE.execute(ac.getCode(),dbDataMap);
						String fname = ac.getField();
						
						MapHelper.setValue(value, fname, val);
						MapHelper.setValue(dbDataMap, fname, val);
						MapHelper.setValue(whenMap, fname, true);
						
						newUpdate = true;	
					}
				}else {
					next.add(trigger);
				}
			}
			
			triggers = next;
		}
	}
	
	private static Map<String,Object> map(Object o){
		if(o instanceof Map){
			return (Map<String,Object>)o;
		}
		return null;
	}
	
	private static void triggerParams(Map<String,Object> whenMap,
			Map<String,Object> dbDataMap,Map<String,Object> updateValue){
		for(String key:updateValue.keySet()){
			if("id".equals(key) || "_id".equals(key)){
				continue;
			}
			
			Object val = updateValue.get(key);
			if(val instanceof Map){
				Map<String,Object> wm2 = new HashMap<String,Object>();
				Object dbVal = dbDataMap.get(key);
				if(!(dbVal instanceof Map)){
					dbVal = val;
				}
				
				triggerParams(wm2,map(dbVal),map(val));
				
				whenMap.put(key,wm2);
			}else if(Utils.isArray(val)){
				for(Object vo : Utils.toArray(val)){
					if(vo instanceof Map){
						//待实现
					}else {
						whenMap.put(key,true);
						dbDataMap.put(key, val);
						break;
					}
				}
			}else if(val != null){
				if(whenMap != null){
					whenMap.put(key,true);
				}
				
				if(dbDataMap != null){
					dbDataMap.put(key,val);
				}
			}
		}
	}
	
	public Map<String, Object> save(Map value, String collName) {
		value = this.toDBModel(value, collName);

		BasicDBObject doc = this.build(value);
		if (doc == null) {
			return null;
		}
		
		this.getDBCollection(collName).save(doc);

		value.put("id", doc.getObjectId("_id").toByteArray());

		return toMap(doc, collName);
	}

	public int remove(Map value, String collName) {
		BasicDBObject doc = this.build(value);
		if (doc == null) {
			return 0;
		}

		WriteResult result = this.getDBCollection(collName).remove(doc);
		return result.getN();
	}
	
	public int remove(String oid, String collName) {
		WriteResult result = this.getDBCollection(collName).remove(
				this.getObjectId(oid));
		return result.getN();
	}

	public void removeAll(String collName) {
		this.getDBCollection(collName).remove(new BasicDBObject());
	}

	protected Map<String, Object> toMap(DBObject doc, String collName) {
		Map<String, Object> dataMap = doc.toMap();
		mergeLinkedObject(dataMap, collName);
		return dataMap;
	}

	public Map<String, Object> getByID(String oid, String collName) {
		DBCursor cursor = this.getDBCollection(collName).find(
				this.getObjectId(oid));

		while (cursor.hasNext()) {
			BasicDBObject doc = (BasicDBObject) cursor.next();
			return toMap(doc, collName);
		}

		return this.emptyResult();
	}
	
	//================ aggregate ==================
	
	public Map<String, Object> aggregateOne(List<Map<String, Object>> query,
			String collName) {
		List<Map<String, Object>> results = aggregateByMap(query, collName);
		if (results != null && results.size() > 0) {
			return results.get(0);
		}
		return null;
	}

	public List<Map<String, Object>> aggregateByMap(
			List<Map<String, Object>> query, String collName) {
		if (query == null || query.isEmpty()) {
			return this.emptyResults();
		}

		List<DBObject> pipeline = new ArrayList<DBObject>();
		for (Map<String, Object> map : query) {
			if (map.isEmpty())
				continue;

			pipeline.add(new BasicDBObject(map));
		}

		return this.aggregate(pipeline, collName);
	}
	
	public List<Map<String, Object>> aggregate(List<DBObject> pipeline,
			String collName) {
		if (pipeline == null || pipeline.isEmpty()) {
			return this.emptyResults();
		}

		DBCollection db = this.getDBCollection(collName);
		AggregationOutput aout = db.aggregate(pipeline);
		Iterator<DBObject> ie = aout.results().iterator();

		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		while (ie.hasNext()) {
			DBObject dbo = ie.next();
			results.add(toMap(dbo, collName));
		}
		return results;
	}
	
	private List<Map<String, Object>> emptyResults() {
		return new ArrayList<Map<String, Object>>();
	}

	/***
	 * 分页查询
	 * **/
	public List<Map<String, Object>> findByMap(Map<String, Object> query,
			Map<String, Object> sort, Pager pg, String collName) {
		return this.find(build(query), build(sort), pg, collName);
	}

	/***
	 * 分页查询
	 * **/
	public List<Map<String, Object>> find(BasicDBObject query,
			BasicDBObject sort, Pager pg, String collName) {
		if (pg == null) {
			pg = new Pager();
		}

		int limit = pg.getPageSize();
		int skip = (pg.getCurPage() - 1) * pg.getPageSize();

		DBCursor cursor = this.getDBCollection(collName).find(query).sort(sort)
				.skip(skip).limit(limit);

		int totalRow = cursor.count();
		pg.setTotalRow(totalRow);

		return this.readCursor(cursor, collName);
	}

	


	private String getLinkedCollectionName(String linkedObjectId) {
		ObjectMetadata linkMeta = this.metaDao.getByID(linkedObjectId);
		if (linkMeta != null) {
			return linkMeta.getName();
		}
		return null;
	}

	
}
