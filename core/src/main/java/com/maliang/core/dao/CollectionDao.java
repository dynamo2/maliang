package com.maliang.core.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.ArrayUtils;
import org.bson.types.ObjectId;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextListener;

import com.maliang.core.arithmetic.AE;
import com.maliang.core.arithmetic.ArithmeticExpression;
import com.maliang.core.call.CallBack;
import com.maliang.core.model.FieldType;
import com.maliang.core.model.ModelType;
import com.maliang.core.model.ObjectField;
import com.maliang.core.model.ObjectMetadata;
import com.maliang.core.model.Trigger;
import com.maliang.core.model.TriggerAction;
import com.maliang.core.service.MapHelper;
import com.maliang.core.ui.controller.Pager;
import com.maliang.core.util.StringUtil;
import com.maliang.core.util.Utils;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

public class CollectionDao extends BasicDao {
	private TreeModelDao treeDao = new TreeModelDao();
	
	public static void main2(String[] args) {
		
		RequestContextListener rc = null;

		String str = "db.Region.aggregateOne([{$match:{province.name:'我是'}},{ $unwind :'$province.cities'},"
				+ "{$group:{_id:{$cond:{if:{$eq:['$province.cities.name','缂佸秴鍙�']},then:{ $ifNull:[ '$province.cities.districts',[]]},else:[]}}}},"
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
		
		/**
		 * 添加createdDate，modifiedDate
		 * **/
		if(!(doc.containsKey("id") || doc.containsKey("_id"))){
			doc.put("createdDate",new Date());
		}
		doc.put("modifiedDate",new Date());
		
		this.insertTrigger(value, collName);
		this.getDBCollection(collName).save(doc);

		value.put("id", doc.getObjectId("_id").toByteArray());
		
		/***
		 * Log
		 * **/
		this.logDao.insertLog(doc,collName);

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

	public Map<String, Object> getByID(final String oid, String collName) {
		final DBCollection dbc = this.getDBCollection(collName);
		DBCursor cursor = dbc.find(this.getObjectId(oid));
		while (cursor.hasNext()) {
			BasicDBObject doc = (BasicDBObject) cursor.next();
			
			return toMap(doc, collName);
		}

		return this.emptyResult();
	}
	
	public static void main(String[] args) {
		String s = "addToParams({ol:db.Order.get('5937d229961fdd8b899f4d7c'),ps:ol.items.product})";
		s = "db.Order.get('5937d229961fdd8b899f4d7c')";
		
		Object o = AE.execute(s);

		Object os = MapHelper.readValue(o,"items.product");
		System.out.println(os);
		//System.out.println(MapHelper.readValue(o,"ps"));
		//System.out.println(MapHelper.readValue(o,"p.items.product"));
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
		
		query = (List)this.parseQueryData(query, collName);
		
		List<DBObject> pipeline = new ArrayList<DBObject>();
		for (Object map : query) {
			if(Utils.isEmpty(map)){
				continue;
			}
			
			if(map instanceof Map){
				pipeline.add(new BasicDBObject((Map)map));
			}

			if(map instanceof BasicDBObject){
				pipeline.add((BasicDBObject)map);
			}
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
	 * 閸掑棝銆夐弻銉嚄
	 * **/
	public List<Map<String, Object>> findByMap(Map<String, Object> query,
			Map<String, Object> sort, Pager pg, String collName) {
		BasicDBObject bq = null;
		Object val = this.parseQueryData(query, collName);
		if(val instanceof Map){
			bq = build((Map)val);
		}
		if(val instanceof BasicDBObject){
			bq = (BasicDBObject)val;
		}
		bq = projectQuery(bq,collName);
		
		BasicDBObject dbSort = this.build(sort);
		if(dbSort == null){
			dbSort = new BasicDBObject("_id",-1);
		}
		if(!dbSort.containsKey("_id")){
			dbSort.put("_id",-1);
		}

		return this.find(bq, dbSort, pg, collName);
	}
	
	/***
	 * 鐎涙劙娉﹂惃鍕瀻妞ゅ灚鐓＄拠锟�
	 * 
	 * "db.Warehouse.aggregateOne([{$unwind :'$stores'},
	 * {$group:{_id:'$stores.product',totalStore:{$sum:'$stores.num'}}},{$match:{_id:'574c02d87a779392fcea0c9b'}}])";
	 * **/
	public List findByMap(Map<String, Object> match,Map<String, Object> query,
			Map<String, Object> sort, Pager pg, String collName,String innerName) {
		DBCollection db = this.getDBCollection(collName);
		
		List<DBObject> pipeline = new ArrayList<DBObject>();
		List<DBObject> countPipe = new ArrayList<DBObject>();
		
		/**
		 * 缁涙盯锟藉顑囨稉锟界仦鍌涙殶閹癸拷
		 * 缁锟藉绱版潻鍛达拷鐔虹級鐏忓繑鏆熼幑顔垮瘱閸ワ拷
		 * ***/
		if(match == null){
			match = query;
		}
		match = (Map<String, Object>)this.parseQueryData(match, collName);
		BasicDBObject bmatch = build(match);
		bmatch = projectQuery(bmatch,collName);
		if(bmatch != null){
			pipeline.add(new BasicDBObject("$match",bmatch));
			countPipe.add(new BasicDBObject("$match",bmatch));
		}
		
		/**
		 * $unwind
		 * */
		pipeline.add(new BasicDBObject("$unwind","$"+innerName+""));
		countPipe.add(new BasicDBObject("$unwind","$"+innerName+""));
		
		/**
		 * 閸栧綊鍘�$unwind閸氬海娈戦弫鐗堝祦
		 * */
		query = (Map<String, Object>)this.parseQueryData(query, collName);
		BasicDBObject bquery = build(query);
		if(bquery != null){
			pipeline.add(new BasicDBObject("$match",bquery));
			countPipe.add(new BasicDBObject("$match",bquery));
		}
		
		/**
		 * count
		 * page.totalRow
		 * **/
		if(pg != null){
			countPipe.add(new BasicDBObject("$count","totalCount"));
			AggregationOutput cOut = db.aggregate(countPipe);
			Iterator<DBObject> cie = cOut.results().iterator();
			while (cie.hasNext()) {
				DBObject dbo = cie.next();
				int  totalCount = (Integer)dbo.get("totalCount");
				pg.setTotalRow(totalCount);
			}
		}
		

		/**
		 * $sort
		 * */
		BasicDBObject bsort = build(sort);
		if(bsort != null){
			pipeline.add(new BasicDBObject("$sort",bsort));
		}

		/**
		 * $project
		 * **/
		Map project = new HashMap();
		project.put("_id",0);
		project.put(innerName,1);
		pipeline.add(new BasicDBObject("$project",project));
		
		/**
		 * $skip,$limit
		 * **/
		if(pg != null){
			int limit = pg.getPageSize();
			int skip = (pg.getCurPage() - 1) * pg.getPageSize();
			pipeline.add(new BasicDBObject("$skip",skip));
			pipeline.add(new BasicDBObject("$limit",limit));
		}
		
		System.out.println("---------- pipeline : " + pipeline);

		if (pipeline == null || pipeline.isEmpty()) {
			return this.emptyResults();
		}
		
		AggregationOutput aout = db.aggregate(pipeline);
		Iterator<DBObject> ie = aout.results().iterator();

		List<Object> results = new ArrayList<Object>();
		while (ie.hasNext()) {
			DBObject dbo = ie.next();
			Object innerObj = null;
			Object val = null;
			if(dbo != null && dbo.containsField(innerName)){
				innerObj = dbo.get(innerName);
				if(innerObj != null && innerObj instanceof DBObject){
					val = this.toMap((DBObject)innerObj, collName, innerName);
				}
			}
			results.add(val);
		}
		
		return results;
	}
	
	/***
	 * 閸掑棝銆夐弻銉嚄
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

	/**
	 * update by $set
	 * **/
	public int update(Map qMap, Map sMap, String collName) {
		return update(qMap,sMap,collName,"$set");
	}
	
	/**
	 * update by $set
	 * **/
	public int update(Map qMap, Map updateMap, String collName,String updateCmd) {
		if(Utils.isEmpty(updateMap)){
			return 0;
		}
		
		BasicDBObject query = null;
		if(qMap == null){
			query = new BasicDBObject();
		}else {
			if(qMap instanceof BasicDBObject){
				query = (BasicDBObject)qMap;
			}else {
				query = this.build(this.buildDBQueryMap(qMap, null));
			}
		}
		
		BasicDBObject updateVal = null;
		if(updateMap instanceof BasicDBObject){
			updateVal = (BasicDBObject)updateMap;
		}else {
			updateVal = this.build(this.buildDBQueryMap(updateMap, null));
		}
		
		BasicDBObject updateObj = new BasicDBObject(updateCmd, updateVal);
//		BasicDBObject modifiedDate = new BasicDBObject("modifiedDate",new Date());
//		updateObj.put("$set", modifiedDate);
		
		DBCollection db = this.getDBCollection(collName);
		WriteResult result  = db.updateMulti(query,updateObj);
		
		System.out.println("update UpsertedId : " + result.getUpsertedId());
		
		return result.getN();
	}
	
	public int deleteArrayDocument(String collName,String arrayFieldName,String docId){
		Map query = this.buildInnerGetMap(arrayFieldName, docId);
		BasicDBObject pullMap = new BasicDBObject(arrayFieldName, new BasicDBObject("_id",new ObjectId(docId)));
		
		return this.update(query, pullMap, collName,"$pull");
	}

	public int updateAll(Map value, String collName) {
		return this.update(null, value, collName);
	}
	
	/***
	 * 移动树结构下某节点的所有子节点
	 * **/
//	protected void moveChildTree(Map oldVal,Map newVal,String collName){
//		/***
//		 * 判断节点的父对象是否有改变(oldVal.parent==newVal.parent)
//		 * 改变：变更其所有子节点的路径（_path_）
//		 * 未改变：不操作
//		 * **/
//		Object oldParent = MapHelper.readValue(oldVal,"_parent_.id");
//		Object newParent = MapHelper.readValue(oldVal,"_parent_");
//		if(oldParent.equals(newParent)){
//			return;
//		}
//		
//		/***
//		 * 读取路径：
//		 * 旧路径：oldVal._path_
//		 * 新路径：newVal._path_
//		 * **/
//		String oid = this.readObjectIdToString(oldVal);
//		Object oldPath = MapHelper.readValue(oldVal,"_path_.id");
//		Object newPath = MapHelper.readValue(newVal,"_path_");
//		
//		((List)oldPath).add(oid);
//		((List)newPath).add(oid);
//		
//		System.out.println("moveChildTree oldPath : " + oldPath);
//		System.out.println("moveChildTree newPath : " + newPath);
//		
//		/**
//		 * 根据旧路径获得要操作的所有子节点的ID
//		 * **/
//		//String query = "{_path_:{$all:oldPath}}";
//		BasicDBObject query = new BasicDBObject(
//				this.newMap("_path_",this.newMap("$all",oldPath)));
//		
//		List<Map<String,Object>> list = this.find(query, collName);
//		if(Utils.isEmpty(list)){
//			return;
//		}
//		
//		List<ObjectId> ids = new ArrayList<ObjectId>();
//		for(Map<String,Object> data : list){
//			ids.add(new ObjectId((String)data.get("id")));
//		}
//		query = this.build(this.newMap("_id",this.newMap("$in",ids)));
//		
//		
//		/***
//		 * 删除子节点的旧路径
//		 * **/
//		//String pull = "{$pull:{_path_:{$in:oldPath}}}";
//		BasicDBObject pull = new BasicDBObject(
//				this.newMap("$pull", this.newMap("_path_",this.newMap("$in",oldPath))));
//		
//		/***
//		 * 增加新路径
//		 * **/
//		//String push = "{$push:{_path_:{$each:newPath,$position:0}}}";
//		Map pushPath = this.newMap("$each",newPath);
//		pushPath.put("$position",0);
//		BasicDBObject push = new BasicDBObject(
//				this.newMap("$push",this.newMap("_path_",pushPath)));
//
//		DBCollection db = this.getDBCollection(collName);
//		db.updateMulti(query, pull);
//		db.updateMulti(query, push);
//		
//		System.out.println("moveChildTree query : " + query);
//		System.out.println("moveChildTree pull : " + pull);
//		System.out.println("moveChildTree push : " + push);
//		System.out.println("moveChildTree id query list : " + this.find(query, collName));
//	}

	public Map<String, Object> updateBySet(Map value, String collName) {
		value = this.toDBModel(value, collName);
		
		/**
		 * 触发器
		 * **/
		this.updateTrigger(value, collName);

		String id = (String) value.remove("id");
		BasicDBObject query = this.getObjectId(id);
		
		ObjectMetadata meta = this.metaDao.getByName(collName);
		List<Map<String, BasicDBObject>> updates = new ArrayList<Map<String, BasicDBObject>>();
		Map<String, Object> daoMap = buildUpdates(meta.getFields(), value,
				null, updates, query,false);
		
		updates.add(buildSetUpdateMap(query, daoMap));
		
		/***
		 * update log
		 * ***/
		Map<String,Object> oldVal = this.getByID(id, collName);
		Map<String,Object> logVal = this.logUpdateValue(meta.getFields(),oldVal, value);
		
		DBCollection db = this.getDBCollection(collName);
		DBObject result = null;
		List<Map<Object,Object>> resultValueMap = new ArrayList<Map<Object,Object>>();
		for (Map<String, BasicDBObject> um : updates) {
			if (um != null) {
				result = db.findAndModify(um.get("query"), null, null, false,
						um.get("update"), true, false);
			}
		}
		
		/**
		 * 变更树结构中，操作节点的所有子节点的路径
		 * **/
		this.treeDao.updateChildrenPaths(oldVal, value, meta,this);
		
		
		/***
		 * update log
		 * ***/
		this.logDao.updateLog(new ObjectId(id),logVal, collName);

		value.put("id", id);

		return value;
	}
	
	/***
	 * 婢跺嫮鎮婇崗瀹犱粓鐎电钖勯惃鍕蒋娴犺埖鐓￠幍鎾呯窗
	 * 娓氬顩� : db.Order.search({items.product.name:'AQ'})
	 * 婢跺嫮鎮婄紒鎾寸亯閿涳拷 pids: db.Product.find({name:'AQ'}).id
	 * 			 db.Order.search(items.product:{$in:pids})
	 * 
	 * 閺�顖涘瘮闁帒缍婇敍灞界サ婵傛鍙ч懕鏂款槱閻炲棴绱�
	 * 娓氬顩ч敍姝瀊.Order.search({items.product.brand.name:'AQ'})
	 * 婢跺嫮鎮婄紒鎾寸亯閿涳拷 bids:db.Brand.search({name:'AQ'}).id,
	 *           pids:db.Product.search({brand:{$in:bids}}).id,
	 *           db.Order.search(items.product:{$in:pids})
	 * 
	 * **/
	private Object parseLinkedQuery(Object query,String collName){
		if(query instanceof Map){
			BasicDBObject newQuery = new BasicDBObject();
			for(String key :((Map<String,Object>)query).keySet()){
				Object fieldMatch = ((Map<String,Object>)query).get(key);
				
				if(key.startsWith("$")){
					newQuery.put(key, parseLinkedQuery(fieldMatch,collName));
					continue;
				}
				
				BasicDBObject linkedQuery = this.parseLinkedQuery(key, fieldMatch, collName);
				if(linkedQuery != null){
					newQuery.putAll((Map)linkedQuery);
				}else {
					newQuery.put(key, fieldMatch);
				}
			}
			return newQuery;
		}else if(query instanceof List){
			List<Object> newMatchs = new ArrayList<Object>();
			for(Object match:(List)query){
				newMatchs.add(parseLinkedQuery(match,collName));
			}
			return newMatchs;
		}
		return query;
	}
	
	private ObjectField readField(List<ObjectField> fields,String fname){
		if(Utils.isEmpty(fields)){
			return null;
		}
		
		for(ObjectField of : fields){
			if(of.getName().equals(fname)){
				return of;
			}
		}
		
		return null;
	}
	
	private BasicDBObject parseLinkedQuery(String fieldKey,Object fieldMatch,String collName){
		String[] keys = null;
		if(fieldKey.contains(".")){
			keys = fieldKey.split("\\.");
		}else {
			keys = new String[]{fieldKey};
		}
		
		ObjectMetadata ometa = this.metaDao.getByName(collName);
		List<ObjectField> fields = ometa.getFields();
		String parentKey = null;
		for(int i = 0; i < keys.length; i++){
			String key = keys[i];
			
			ObjectField oField = readField(fields,key);
			
			if(oField != null && FieldType.LINK_COLLECTION.is(oField.getType())){
				String linkedCollName = oField.getLinkedObject();
				
				fieldKey = key;
				if(parentKey != null){
					fieldKey = parentKey+"."+fieldKey;
				}

				StringBuilder sb = null;
				for(int ii = i+1; ii < keys.length; ii++){
					if(sb == null){
						sb = new StringBuilder(keys[ii]);
					}else {
						sb.append(".");
						sb.append(keys[ii]);
					}
				}
				if(sb == null){
					return null; 
				}
				
				String linkedKey = sb.toString();
				if(linkedKey.equals("id")){
					linkedKey = "_id";
				}
				BasicDBObject linkedQuery = new BasicDBObject(linkedKey,fieldMatch);
				linkedQuery = (BasicDBObject)this.parseLinkedQuery(linkedQuery, linkedCollName);
				
				List results = this.find(linkedQuery, linkedCollName);
				List ids = (List)MapHelper.readValue(results,"id");
				if(ids == null){
					ids = new ArrayList();
					ids.add(false);
				}
				
				BasicDBObject qq = new BasicDBObject(fieldKey,new BasicDBObject("$in",ids));
				return qq;
			}
			
			if(key.equals("id")){
				key = "_id";
			}
			if(parentKey == null){
				parentKey = key;
			}else parentKey += "."+key;
			
			if(oField != null){
				fields = oField.getFields();
			}
		}
		
		return null;
	}
	/**
	 * 婢跺嫮鎮婇弻銉嚄閺夆�叉娑擃厾娈戦弫鐗堝祦缁鐎�
	 * **/
	protected Object parseQueryData(Object query,String collName){
		if(query == null){
			return null;
		}
		
		if(query instanceof List){
			List newQuery = new ArrayList();
			for(Object obj : (List)query){
				newQuery.add(this.parseQueryData(obj,collName));
			}
			return newQuery;
		}
		
		if(query instanceof Map){
			return parseQueryData((Map<String,Object>)query,collName);
		}
		
		return query;
	}
	
	/**
	 * 婢跺嫮鎮奙ap缁鐎烽弻銉嚄閺夆�叉娑擃厾娈戦弫鐗堝祦缁鐎�
	 * **/
	private Object parseQueryData(Map<String,Object>query,String collName){
		if(query == null){
			return null;
		}
		
		Map<String,Object> newQuery = new HashMap<String,Object>();
		for(String key : query.keySet()){
			Object val = query.get(key);
			
			if(key.startsWith("$")){
				val = parseQueryData(val,collName);
			}else {
				Object fieldType = readFieldType(key,collName);
				val = parseFieldType(fieldType,val);
				
				if(hasLinkedField(key,collName)){
					return this.parseLinkedQuery(key,val, collName);
				}
				
				if(!key.endsWith("._id") && key.endsWith(".id")){
					key = key.substring(0,key.length()-3)+"._id";
				}
			}
			newQuery.put(key,val);
		}
		return newQuery;
	}

	private Object readFieldType(String fieldKey,String collName){
		if(fieldKey.endsWith(".id") || fieldKey.endsWith("._id")){
			return ObjectId.class;
		}
		
		ObjectMetadata meta = this.metaDao.getByName(collName);
		String[] keys = fieldKey.split("\\.");
		List<ObjectField> lastFields = meta.getFields();
		Object resultType = null;
		for(int i = 0; i < keys.length; i++){
			String key = keys[i];
			for(ObjectField of:lastFields){
				if(of.getName().equals(key)){
					if(FieldType.LINK_COLLECTION.is(of.getType())){
						String  linkedKey = StringUtils.arrayToDelimitedString(
												ArrayUtils.subarray(keys,i+1,keys.length), ".");
						if(StringUtil.isEmpty(linkedKey)){
							return of;
						}
						
						String linkedColl = of.getLinkedObject();
						return this.readFieldType(linkedKey, linkedColl);
					}else {
						resultType = of;
						lastFields = of.getFields();
					}
					
					break;
				}
			}
		}
		
		return resultType;
	}
	
	/***
	 * items.id.name
	 * product.name:{$or:[{$in:['AQ','LAMMER']},{$not:'DQ'}]}
	 * **/
	private Object parseFieldType(Object fieldType,Object queryVal){
		if(queryVal == null)return null;
		
		if(queryVal instanceof List){
			List<Object> newVals = new ArrayList<Object>();
			for(Object val:(List)queryVal){
				newVals.add(this.parseFieldType(fieldType,val));
			}
			return newVals;
		}
		
		if(queryVal instanceof Map){
			Map newMap  = new HashMap();
			for(Object key:((Map)queryVal).keySet()){
				Object val = ((Map)queryVal).get(key);
				val = this.parseFieldType(fieldType, val);
				
				newMap.put(key,val);
			}
			return newMap;
		}
		
		if(queryVal instanceof Pattern){
			return queryVal;
		}
		
		if(fieldType instanceof Class){
			if(((Class)fieldType).getCanonicalName().equals("org.bson.types.ObjectId")){
				if(queryVal instanceof ObjectId){
					return queryVal;
				}
				
				return new ObjectId(queryVal.toString());
			}
		}
		
		if(fieldType instanceof ObjectField){
			return parseFieldType((ObjectField)fieldType,queryVal);
		}
		
		return null;
	}
	
	private Object parseFieldType(ObjectField field,Object queryVal){
		if(FieldType.LINK_COLLECTION.is(field.getType())){
			return queryVal.toString();
		}
		
		if(FieldType.STRING.is(field.getType())){
			return queryVal.toString();
		}
		
		if(FieldType.DATE.is(field.getType())){
			if(queryVal instanceof Date){
				return queryVal;
			}
			
			return Utils.parseDate(queryVal.toString());
		}
		
		if(FieldType.INT.is(field.getType())){
			if(queryVal instanceof Number){
				return ((Number)queryVal).intValue();
			}
			
			try {
				return Integer.parseInt(queryVal.toString());
			}catch(Exception e){
				return null;
			}
		}
		
		if(FieldType.DOUBLE.is(field.getType())){
			if(queryVal instanceof Number){
				return ((Number)queryVal).doubleValue();
			}
			
			try {
				return Double.parseDouble(queryVal.toString());
			}catch(Exception e){
				return null;
			}
		}
		
		return null;
	}


	private boolean hasLinkedField(String fieldKey,String collName){
		ObjectMetadata meta = this.metaDao.getByName(collName);
		String[] keys = fieldKey.split("\\.");
		List<ObjectField> lastFields = meta.getFields();
		
		for(String key : keys){
			for(ObjectField of:lastFields){
				if(of.getName().equals(key)){
					if(FieldType.LINK_COLLECTION.is(of.getType())){
						return true;
					}
					lastFields = of.getFields();
					break;
				}
			}
		}
		
		return false;
	}

	public void insertTrigger(Map<String,Object> insertValue, String collName) {
		Map<String,Object> dbDataMap = this.correctData(insertValue, collName, false, true);
		trigger(insertValue,dbDataMap,collName,Trigger.INSERT);
	}
	
	public void updateTrigger(Map<String,Object> updateValue, String collName) {
		Map<String,Object> dbDataMap = this.getByID((String)updateValue.get("id"), collName);
		trigger(updateValue,dbDataMap,collName,Trigger.UPDATE);
	}
	
	/***
	 * 閸樼喎鍨敍锟�
	 * 1. 瀹歌尪顔曠�规氨娈戠仦鐐达拷褌绗夐柌宥咁槻鐠侊紕鐣�
	 * 2. insert濡�崇础閺冭绱濇稉宥堫洬閻╂潄bDataMap閻ㄥ嫬锟斤拷
	 * 
	 * Bug
	 * 1. Utils.clone(): 娑撳秷鍏樻潻娑滎攽濞ｅ崬瀹砪lone
	 * 
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
		triggerParams(whenMap,dbDataMap,value,triggerMode);

		boolean newUpdate = true;
		while(newUpdate && !Utils.isEmpty(triggers)){
			List<Trigger> next = new ArrayList<Trigger>();
			newUpdate = false;
			
			for(Trigger trigger : triggers){
				Object when = AE.execute(trigger.getWhen(),whenMap);
				boolean match = (when != null);
				if(when instanceof Boolean){
					match = (Boolean)when;
				}
				
				if(match){
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
	 * String fieldName閿涙俺袝閸欐垶娲块弬鎵畱鐎涙顔岄崥宥忕礄閸︺劍鏆熺紒鍕厬閻ㄥ嫬鐡у▓闈涙倳閿涳拷
	 * Object fieldVal閿涙俺袝閸欐垶娲块弬鎵畱鐎涙顔岄崐锟�
	 * Object arrayItemData閿涙俺袝閸欐垶娲块弬鎵畱閺佹壆绮嶉惃鍑濨閸樼喎顫愮拋鏉跨秿
	 * String arrayName閿涙碍鏆熺紒鍕畱鐎瑰本鏆ｇ�涙顔岄崥锟�
	 * Map<String,Object> updateVal閿涙艾鐣弫瀵告畱瀵板懏娲块弬鏉款嚠鐠烇拷
	 * 
	 * 娓氬鐡欓敍锟�
	 * 閺囧瓨鏌奜rder: {id:'123456',status:3}
	 * 鐟欙箑褰傞弴瀛樻煀鐎涙顔岄敍姝﹏fo.items.detail.num閿涘矁袝閸欐垶娲块弬鏉匡拷纭风窗20
	 * fieldName閿涙瓰etail.num
	 * fieldVal: 20
	 * arrayItemData
	 * arrayName: info.items
	 * updateVal: {id:'123456',status:3}
	 * 
	 * 鏉╂劘顢戠紒鎾寸亯閿涳拷
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
	 * triggerField: 鐟欙箑褰傞惃鍕摟濞堬拷
	 * fieldVal: 鐎涙顔岄惃鍕拷锟�
	 * fields: 鏉╂瑤閲滅憴锕�褰傞崳銊﹀閸︺劌顕挒锛勬畱ObjectMetadata
	 * dbDataMap: 閸氼垰濮╃憴锕�褰傞崳銊ф畱閸樼喎顫愰弫鐗堝祦鎼存捁顔囪ぐ锟�
	 * triggerLinkedMap: 閸忓疇浠堢�电钖勭悮顐バ曢崣鎴犳畱閺囧瓨鏌婇梿鍡楁値
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
	
	
	
	private static void triggerParams(Map<String,Object> whenMap,
			Map<String,Object> dbDataMap,Map<String,Object> updateValue,int triggerMode){
		boolean updateMode = triggerMode == Trigger.UPDATE;
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
				triggerParams(wm2,toMap(dbVal),toMap(val),triggerMode);
				
				whenMap.put(key,wm2);
			}else if(Utils.isArray(val)){
				whenMap.put(key,true);
				for(Object vo : Utils.toArray(val)){
					if(vo instanceof Map){
						//瀵板懎鐤勯悳锟�
					}else {
						if(updateMode){
							dbDataMap.put(key, val);
						}
						
						break;
					}
				}
			}else if(val != null){
				if(whenMap != null){
					whenMap.put(key,true);
				}
				
				if(dbDataMap != null && updateMode){
					dbDataMap.put(key, val);
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
