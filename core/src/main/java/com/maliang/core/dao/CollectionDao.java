package com.maliang.core.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.maliang.core.arithmetic.AE;
import com.maliang.core.arithmetic.ArithmeticExpression;
import com.maliang.core.model.FieldType;
import com.maliang.core.model.ObjectField;
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
	public static void main2(String[] args) {

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
	
	
	public Map<String, Object> save(Map value, String collName) {
		value = this.toDBModel(value, collName);

		BasicDBObject doc = this.build(value);
		if (doc == null) {
			return null;
		}
		
		System.out.println("****** before trigger value : " + value);
		this.insertTrigger(value, collName);
		System.out.println("****** after trigger value : " + value);
		
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
		this.updateTrigger(value, collName);
		
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

	public static void main(String[] args) {
		String s = "info.items.product";
		int idx = s.indexOf(".",4+1);
		
		System.out.println(idx);
		System.out.println(s.substring(4+1,idx));
		System.out.println(s.substring(idx+1));
		
	}
	
	public void insertTrigger(Map<String,Object> insertValue, String collName) {
		Map<String,Object> dbDataMap = this.correctData(insertValue, collName, false, true);
		
		System.out.println("=== insertTrigger dbDataMap : " + dbDataMap);
		
		trigger(insertValue,dbDataMap,collName,Trigger.INSERT);
	}
	
	public void updateTrigger(Map<String,Object> updateValue, String collName) {
		Map<String,Object> dbDataMap = this.getByID((String)updateValue.get("id"), collName);
		trigger(updateValue,dbDataMap,collName,Trigger.UPDATE);
	}
	
	/***
	 * 原则：
	 * 1. 已设定的属性不重复计算
	 * 
	 * Bug
	 * 1. value : {consigneeInfo:{address:'浙江'}}
	 *    trigger.when: consigneeInfo  计算结果：false
	 *    trigger.when: consigneeInfo.address  计算结果：true
	 *    结论：以上两个when都应为true
	 * ***/
	private void trigger(Map<String,Object> value,Map<String,Object> dbDataMap, String collName,int triggerMode) {
		ObjectMetadata metadata = this.metaDao.getByName(collName);
		List<Trigger> allT = metadata.getTriggers();
		
		if(Utils.isEmpty(allT))return;
		
		List<Trigger> triggers = new ArrayList<Trigger>();
		for(Trigger t : allT){
			if(t.getMode() == triggerMode){
				triggers.add(t);
			}
		}

		if(Utils.isEmpty(triggers))return;

		Map<String,Object> whenMap = newMap();
		Map<String,Map<String,Map<String,Object>>> triggerLinkedMap = new HashMap<String,Map<String,Map<String,Object>>>();
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

						String fname = ac.getField();
						
						if(isArrayField(fname,metadata.getFields())){
							int start = 0;
							int end = fname.length()-1;

							List<ObjectField> fields = metadata.getFields();
							Map<String,Object> dbMap = dbDataMap;
							do{
								int idx = fname.indexOf(".",start);
								if(idx < 0)idx = end;
								
								String firstName = fname.substring(start,idx);
								String lastName = fname.substring(idx+1);
								
								for(ObjectField of:fields){
									if(!of.getName().equals(firstName)){
										continue;
									}

									if(FieldType.ARRAY.is(of.getType())){
										Object dbArrayVal = MapHelper.readValue(dbMap, firstName);
										
										if(Utils.isArray(dbArrayVal)){
											boolean isLinkedField = false;
											boolean isInnerField = false;
											if(FieldType.INNER_COLLECTION.is(of.getElementType())){
												isLinkedField = isLinkedField(lastName,of.getFields());
												isInnerField = true;
											}else if(FieldType.LINK_COLLECTION.is(of.getElementType())){
												isLinkedField = true;
											}
											
											Map<String,Object> params = (Map<String,Object>)Utils.clone(dbDataMap);
											for(Object o:Utils.toArray(dbArrayVal)){
												params.put("this", o);
												Object triggerVal =  AE.execute(ac.getCode(),params);

												if(isLinkedField){
													doTriggerLinkedMap(lastName,triggerVal,of.getFields(),(Map<String,Object>)o,triggerLinkedMap);
												}else if(isInnerField){
													String arrayName = fname.substring(0,idx);
													doArrayInnerUpdate(lastName,triggerVal,o,arrayName,value);
												}
											}
										}
										
										start = end;
									}else if(FieldType.INNER_COLLECTION.is(of.getType())){
										fields = of.getFields();
										dbMap = (Map<String,Object>)MapHelper.readValue(dbMap, firstName);
									}
									
									break;
								}

								start = idx+1;
							}while(start < end);
						}else {
							Object val = AE.execute(ac.getCode(),dbDataMap);
							if(isLinkedField(fname,metadata.getFields())){
								doTriggerLinkedMap(fname,val,metadata.getFields(),dbDataMap,triggerLinkedMap);
							}else {
								MapHelper.setValue(value, fname, val);
								MapHelper.setValue(dbDataMap, fname, val);
								MapHelper.setValue(whenMap, fname, true);
							}
						}
						
						newUpdate = true;
					}
				}else {
					next.add(trigger);
				}
			}
			
			triggers = next;
		}
		
		///update triggerLinkedMap;
		for(String k:triggerLinkedMap.keySet()){
			Map<String,Map<String,Object>> vals = triggerLinkedMap.get(k);
			if(Utils.isEmpty(vals))continue;
			
			for(Map<String,Object> val:vals.values()){
				this.updateBySet(val,k);
			}
		}
	}
	
	/**
	 * String fieldName：触发更新的字段名（在数组中的字段名）
	 * Object fieldVal：触发更新的字段值
	 * Object arrayItemData：触发更新的数组的DB原始记录
	 * String arrayName：数组的完整字段名
	 * Map<String,Object> updateVal：完整的待更新对象
	 * 
	 * 例子：
	 * 更新Order: {id:'123456',status:3}
	 * 触发更新字段：info.items.detail.num，触发更新值：20
	 * fieldName：detail.num
	 * fieldVal: 20
	 * arrayItemData
	 * arrayName: info.items
	 * updateVal: {id:'123456',status:3}
	 * 
	 * 运行结果：
	 * updateVal: {id:'123456',status:3,info:{items:[{id:'654321',detail:{num:20}}]}}
	 * 
	 * ***/
	private void doArrayInnerUpdate(String fieldName,Object fieldVal,Object arrayItemData,String arrayName,Map<String,Object> updateVal){
		Map<String,Object> innerUpdateMap = newMap();
		innerUpdateMap.put("id", MapHelper.readValue(arrayItemData, "id"));
		MapHelper.setValue(innerUpdateMap, fieldName, fieldVal);
		
		Object arrayVal = MapHelper.readValue(updateVal, arrayName);
		if(arrayVal instanceof Map){
			Object old = arrayVal;
			arrayVal = new ArrayList<Map<String,Object>>();
			((List)arrayVal).add(old);
		}else {
			arrayVal = new ArrayList<Map<String,Object>>();
		}
		
		((List)arrayVal).add(innerUpdateMap);
		MapHelper.setValue(updateVal, arrayName, arrayVal);
	}
	
	/**
	 * triggerField: 触发的字段
	 * fieldVal: 字段的值
	 * fields: 这个触发器所在对象的ObjectMetadata
	 * dbDataMap: 启动触发器的原始数据库记录
	 * triggerLinkedMap: 关联对象被触发的更新集合
	 * ****/
	private void doTriggerLinkedMap(String triggerField,Object fieldVal,List<ObjectField> fields,Map<String,Object> dbDataMap,
			Map<String,Map<String,Map<String,Object>>> triggerLinkedMap){
		Map<String,Object> linkedMap = readTriggerLinkedMap(triggerField,fields,dbDataMap);
		if(linkedMap == null)return;
		
		Map<String,Object> dbLinked = (Map<String,Object>)linkedMap.get("value");
		if(dbLinked == null)return;
		
		String lid = (String)dbLinked.get("id");
		String linkedCollName = (String)linkedMap.get("collName");
		Map<String,Map<String,Object>> collMap = triggerLinkedMap.get(linkedCollName);
		if(collMap == null){
			collMap = new HashMap<String,Map<String,Object>>();
			triggerLinkedMap.put(linkedCollName, collMap);
		}
		
		Map<String,Object> updateLinked = collMap.get(lid);
		if(updateLinked == null){
			updateLinked = newMap();
			updateLinked.put("id", lid);
			
			collMap.put(lid, updateLinked);
		}
		
		String fieldName = (String)linkedMap.get("fieldName");
		MapHelper.setValue(updateLinked, fieldName, fieldVal);
	}
	
	private static boolean isArrayField(String fname,List<ObjectField> fields){
		int idx = fname.indexOf(".");
		if(idx < 0)idx = fname.length()-1;
		String firstName = fname.substring(0,idx);
		for(ObjectField of:fields){
			if(of.getName().equals(firstName)){
				if(FieldType.ARRAY.is(of.getType())){
					return true;
				}
				
				if(FieldType.INNER_COLLECTION.is(of.getType())){
					String lastName = fname.substring(idx+1);
					return isLinkedField(lastName,of.getFields());
				}
			}
		}
		return false;
	}

	private static boolean isLinkedField(String fname,List<ObjectField> fields){
		int idx = fname.indexOf(".");
		if(idx < 0)idx = fname.length()-1;
		String firstName = fname.substring(0,idx);
		for(ObjectField of:fields){
			if(of.getName().equals(firstName)){
				if(FieldType.LINK_COLLECTION.is(of.getType())){
					return true;
				}
				
				if(FieldType.INNER_COLLECTION.is(of.getType())
						|| (FieldType.ARRAY.is(of.getType()) && FieldType.INNER_COLLECTION.is(of.getElementType()))){
					String lastName = fname.substring(idx+1);
					return isLinkedField(lastName,of.getFields());
				}
			}
		}
		return false;
	}

	private Map<String,Object> readTriggerLinkedMap(String fname,List<ObjectField> fields,Map<String,Object> dbValue){
		int idx = fname.indexOf(".");
		if(idx < 0)idx = fname.length()-1;
		String firstName = fname.substring(0,idx);
		String lastName = fname.substring(idx+1);
		for(ObjectField of:fields){
			if(of.getName().equals(firstName)){
				Object val = MapHelper.readValue(dbValue, firstName);
				
				if(FieldType.LINK_COLLECTION.is(of.getType())){
					if(val != null && !(val instanceof Map)){
						val = this.getLinkedObject(val.toString(), of.getLinkedObject());
					}
					
					Map<String,Object> returnMap = newMap();
					returnMap.put("fieldName",lastName);
					returnMap.put("value",val);
					returnMap.put("collName",of.getLinkedObject());
					return returnMap;
				}
				
				if(FieldType.INNER_COLLECTION.is(of.getType())){
					if(val != null && val instanceof Map){
						return readTriggerLinkedMap(lastName,of.getFields(),(Map<String,Object>)val);
					}
				}
				
				//Array linked object
//				if(FieldType.ARRAY.is(of.getType()) && FieldType.INNER_COLLECTION.is(of.getElementType())){
//					String lastName = fname.substring(idx);
//					return isLinkedField(lastName,of.getFields());
//				}
				
				break;
			}
		}
		
		return null;
	}
	
	private static Map<String,Object> toMap(Object o){
		if(o instanceof Map){
			return (Map<String,Object>)o;
		}
		return null;
	}
	
	private static boolean isId(String key){
		return "id".equals(key) || "_id".equals(key);
	}
	
	private static Map<String,Object> newMap(){
		return new HashMap<String,Object>();
	}
	
	private static void triggerParams(Map<String,Object> whenMap,
			Map<String,Object> dbDataMap,Map<String,Object> updateValue){
		for(String key:updateValue.keySet()){
			if(isId(key)){
				continue;
			}
			
			Object val = updateValue.get(key);
			if(val instanceof Map){
				Object dbVal = dbDataMap.get(key);
				if(!(dbVal instanceof Map)){
					dbVal = val;
				}

				Map<String,Object> wm2 = newMap();
				triggerParams(wm2,toMap(dbVal),toMap(val));
				
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
	
	

	


	private String getLinkedCollectionName(String linkedObjectId) {
		ObjectMetadata linkMeta = this.metaDao.getByID(linkedObjectId);
		if (linkMeta != null) {
			return linkMeta.getName();
		}
		return null;
	}

	
}
