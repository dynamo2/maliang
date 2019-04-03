package com.maliang.core.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import com.maliang.core.arithmetic.AE;
import com.maliang.core.call.CallBack;
import com.maliang.core.model.FieldType;
import com.maliang.core.model.Log;
import com.maliang.core.model.ModelType;
import com.maliang.core.model.ObjectField;
import com.maliang.core.model.ObjectMetadata;
import com.maliang.core.model.Project;
import com.maliang.core.model.UCType;
import com.maliang.core.model.UCValue;
import com.maliang.core.service.MR;
import com.maliang.core.service.MapHelper;
import com.maliang.core.util.StringUtil;
import com.maliang.core.util.Utils;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

public class BasicDao extends AbstractDao{
	private static String META_KEY = "_meta";
	
	protected ObjectMetadataDao metaDao = new ObjectMetadataDao();
	protected UCTypeDao uctypeDao = new UCTypeDao();
	//protected CollectionDao collDao = new CollectionDao();
	public static final String[] DB_KEYWORDS = {"if","then","else"};
	public LogDao logDao = new LogDao();

	public static boolean isDBKeyword(String name){
		if(StringUtil.isEmpty(name))return false;
		name = name.trim();
		
		if(name.startsWith("$"))return true;
		for(String key : DB_KEYWORDS){
			if(key.equals(name)){
				return true;
			}
		}
		return false;
	}
	
	protected static Map<String,Object> newMap(){
		return new HashMap<String,Object>();
	}
	
	protected static Map<String,Object> newMap(String key,Object val){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put(key, val);
		return map;
	}

	public void save(BasicDBObject doc,String collName) {
		this.getDBCollection(collName).save(doc);
	}
	
	public Object delayInnerObject(final String collName,final String innerName,final String oid){
		return new CallBack(){
			private Object obj;
			private int num = 0;
			public Object doCall(){
				if(obj == null && num < 3){
					this.obj = getInnerObject(collName,innerName,oid);
				}
				
				num++;
				return this.obj;
			}
		};
	}
	
	
	public Map<String,Object> getInnerObject(String collName,String innerName,String oid){
		List<DBObject> pipe = new ArrayList<DBObject>();
		
		System.out.println("------ oid : " + oid);
		BasicDBObject query = new BasicDBObject(innerName+"._id",new ObjectId(oid));
		
		pipe.add(new BasicDBObject("$match",query));
		pipe.add(new BasicDBObject("$unwind","$"+innerName));
		pipe.add(new BasicDBObject("$match",query));
		pipe.add(new BasicDBObject("$project",new BasicDBObject().append(innerName,1).append("_id",0)));
		
		AggregationOutput aout = this.getDBCollection(collName).aggregate(pipe);
		Iterator<DBObject> ie = aout.results().iterator();
		while(ie.hasNext()){
			return this.toMap((BasicDBObject)ie.next().get(innerName), collName,innerName);
		}
		
		return null;
	}
	
	public Map<String,Object> innerObjectById(Map<String,Object> query,String collName){
		Map<String,Object> dbQuery = buildDBQueryMap(query,null);
		
		List<Map<String,Object>> results = this.findByMap(dbQuery, collName);
		
		if(results != null && results.size() > 0){
			Map<String,Object> returnObject = findInnerById(results.get(0),dbQuery);
			if(returnObject != null){
				return returnObject;
			}
		}
		return null;
	}
	
	public List<Map<String,Object>> findByMap(Map<String,Object> query,String collName){
		BasicDBObject bq = build(query);
		bq = projectQuery(bq,collName);
		
		return this.find(bq, collName);
	}
	
	/**
	 * 添加project条件
	 * **/
	protected BasicDBObject projectQuery(BasicDBObject query,String collName){
		if(this.isSystemCollection(collName)){
			Project project = getSessionProject();
			if(project != null){
				if(query == null){
					query = new BasicDBObject();
				}
				
				query.append("project", project.getId().toString());
			}
		}
		return query;
	}
	
	public List<Map<String,Object>> find(BasicDBObject query,String collName){
		DBCursor cursor = this.getDBCollection(collName).find(query);
		
		return readCursor(cursor,collName);
	}
	
	protected List<Map<String,Object>> readCursor(DBCursor cursor,String collName){
		List<Map<String,Object>> results = new ArrayList<Map<String,Object>>();
		for(DBObject dob : cursor.toArray()){
			//results.add(toMap(dob,collName));
			results.add(dob.toMap());
		}
		return results;
	}
	
	public BasicDBObject buildBasicDBObject(Map<String,Object> datas,String objectUniqueMark){
		//String collName = (String)datas.get(OBJECT_METADATA_KEY);
		
		ObjectMetadata metadata = this.metaDao.getByUniqueMark(objectUniqueMark);
		
		return null;
	}
	
	public static Map<String,Object> findInnerById(Map<String,Object> rootObject,Map<String,Object> dbQuery){
		Map<String,Object> returnObject = null;
		
		for(String key : dbQuery.keySet()){
			if(key.endsWith("_id")){
				String idVal = dbQuery.get(key).toString();
				
				Object innerObj = rootObject;
				if(key.lastIndexOf(".") > 0){
					String field = key.substring(0,key.lastIndexOf("."));
					innerObj = MapHelper.readValue(rootObject, field);
				}
				
				if(innerObj instanceof List){
					for(Object val : (List)innerObj){
						if(val instanceof Map){
							if(idVal.equals(((Map) val).get("id"))){
								returnObject = (Map<String,Object>)val;
								//returnObject.put("_RootObject_", rootObject);
								
								return returnObject;
							}
						}
					}
				}else if(innerObj instanceof Map){
					if(idVal.equals(((Map) innerObj).get("id"))){
						returnObject = (Map<String,Object>)innerObj;
						returnObject.put("_RootObject_", rootObject);
						return returnObject;
					}
				}
			}
		}
		return returnObject;
	}
	
	/**
	 * update by $set
	 * **/
	public int update(Map qMap, Map sMap, String collName) {
		return this.update(qMap, sMap, collName,"$set");
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
	
	protected BasicDBObject toDBObject(Map<String,Object> query){
		if(query == null || query.isEmpty()){
			return null;
		}
		
		return new BasicDBObject(query);
	}
	

	protected BasicDBObject build(Map<String,Object> query){
		if(query == null || query.isEmpty()){
			return null;
		}
		
		return new BasicDBObject(query);
	}
	
	/***
	 * 灏唟items:{product:{name:'AQ'}}}杞垚锛歿items.product.name:'AQ'}
	 * **/
	protected static Map<String,Object> buildDBQueryMap(Map<String,Object> queryMap,String prefix){
		Map<String,Object> daoMap = new HashMap<String,Object>();
		String preName = "";
		if(!StringUtil.isEmpty(prefix)){
			preName = prefix+".";
		}
		
		if(queryMap.get("id") != null){
			try {
				daoMap.put(preName+"_id", new ObjectId(queryMap.remove("id").toString()));
			}catch(IllegalArgumentException ae){}
		}
		
		boolean isKeyword = false;
		for(String fname : queryMap.keySet()){
			isKeyword = isDBKeyword(fname);
			
			String key = preName+fname;
			Object value = queryMap.get(fname);
			if(value != null && value instanceof Map && ((Map)value).size() > 0){
				Map<String,Object> valMap = (Map<String,Object>)value;

				Map<String,Object> m = buildDBQueryMap((Map<String,Object>)value,isKeyword?null:key);
				if(isKeyword){
					daoMap.put(fname,m);
				}else {
					daoMap.putAll(m);
				}
			}else {
				if(isKeyword){
					key = fname;
				}
				
				daoMap.put(key, value);
			}
		}
		
		return daoMap;
	}
	
	
	protected Map<String,Object> logUpdateArrayItemValue(ObjectField field,List<Map<String,Object>> oldVal,Map<String,Object> newVal){
		String id = this.readObjectIdToString(newVal);
		
		Map<String,Object> oo = readItemFromArray(id,oldVal);
		
		Map<String,Object> oolv = this.logUpdateValue(field.getFields(),oo,newVal);
		
		oolv.put("id",oo.get("id"));
		return oolv;
	}
	
	protected Map<String,Object> readItemFromArray(String id,List<Map<String,Object>> items){
		if(id == null){
			return null;
		}
		
		if(Utils.isEmpty(items)){
			return null;
		}
		
		for(Map<String,Object> o : items){
			Object temp = this.readObjectIdToString(o);
			if(id.equals(temp)){
				return o;
			}
		}
		return null;
	}
	
	protected boolean compareDBArray(List<Object> x,List<Object> y){
		if(x == null){
			if(y == null){
				return true;
			}
			return false;
		}
		
		if(y == null){
			return false;
		}
		
		if(x.size() != y.size()){
			return false;
		}
		
		for(Object ox:x){
			if(!y.contains(ox)){
				return false;
			}
		}
		return true;
	}
	
	protected List<Object> wrapList(Object o){
		return this.wrapList(o,false);
	}
	
	protected List<Object> wrapList(Object o,boolean force){
		if(o instanceof List && !force){
			return (List<Object>)o;
		}
		
		List<Object> list = new ArrayList<Object>();
		list.add(o);
		return list;
	}
	
	
	protected Map<String,Object> logUpdateValue(List<ObjectField> fields,Map<String,Object> oldVal,Map<String,Object> newVal){
		Map<String,Object> logVal = new HashMap<String,Object>();
		
		for(String k : newVal.keySet()){
			if(k.startsWith(".")){
				continue;
			}
			
			if(k.equals("id") || k.equals("_id")){
				continue;
			}
			
			ObjectField field = readObjectField(fields,k);
			if(field == null){
				continue;
			}
			
			Object ov = oldVal.get(k);
			Object nv = newVal.get(k);
			
			if(!oldVal.containsKey(k)){
				logVal.put(k,logVal(Log.NOT_EXIST,nv));
				continue;
			}

			if(FieldType.INNER_COLLECTION.is(field.getType())){
				if(ov == null){
					logVal.put(k,logVal(null,nv));
					continue;
				}
				
				if(nv != null){
					Map<String,Object> ilv = logUpdateValue(field.getFields(),(Map<String,Object>)ov,(Map<String,Object>)nv); 
					if(!ilv.isEmpty()){
						logVal.put(k, ilv);
					}
				}
				
				continue;
			}
			
			if(FieldType.ARRAY.is(field.getType())){
				nv = wrapList(nv);
				
				if(FieldType.INNER_COLLECTION.is(field.getElementType())){
					List<Map<String,Object>> oldItems = (List<Map<String,Object>>)ov;
					
					List<Map<String,Object>> pushes = new ArrayList<Map<String,Object>>();
					List<Map<String,Object>> sets = new ArrayList<Map<String,Object>>();
					for(Map<String,Object> mv : (List<Map<String,Object>>)nv){
						String id = this.readObjectIdToString(mv);
						Map<String,Object> oo = readItemFromArray(id,oldItems);
						
						if(Utils.isEmpty(oo)){
							pushes.add(mv);
						}else {
							Map<String,Object> oolv = this.logUpdateValue(field.getFields(),oo,mv);
							oolv.put("id",oo.get("id"));
							sets.add(oolv);
						}
					}
					
					Map map = new HashMap();
					if(!Utils.isEmpty(pushes)){
						map.put(Log.ARRAY_PUSH,pushes);
					}
					
					if(!Utils.isEmpty(sets)){
						map.put(Log.ARRAY_SET,sets);
					}
					
					logVal.put(k,map);
				}
				
				if(FieldType.LINK_COLLECTION.is(field.getElementType())){
					//List<Object> oldItems = (List<Object>)ov;
				}
				continue;
			}
			
			if(FieldType.LINK_COLLECTION.is(field.getType())){
				if(ov != null && ov instanceof CallBack){
					ov = ((CallBack)ov).doCall();
				}
				
				if(ov != null && ov instanceof Map){
					ov = readObjectIdToString((Map)ov);
				}
				
				if(nv != null && nv instanceof CallBack){
					nv = ((CallBack)nv).doCall();
				}
				
				if(nv != null && nv instanceof Map){
					nv = readObjectIdToString((Map)nv);
				}
			}
			
			if(FieldType.VARIABLE_LINK.is(field.getType())){
				if(ov != null && ov instanceof Map){
					String id = readObjectIdToString((Map)ov);
					String mtype = readMetadataType((Map)ov);
					
					if(!StringUtil.isEmpty(id) && !StringUtil.isEmpty(mtype)){
						ov = mtype+","+id;
					}
				}
				
				if(nv != null && nv instanceof Map){
					String id = readObjectIdToString((Map)nv);
					String mtype = readMetadataType((Map)nv);
					
					if(!StringUtil.isEmpty(id) && !StringUtil.isEmpty(mtype)){
						nv = mtype+","+id;
					}
				}
			}
			
			
			if(nv == null){
				if(ov == null){
					if(!oldVal.containsKey(k)){
						logVal.put(k,logVal(Log.NOT_EXIST,null));
					}
				}else {
					logVal.put(k,logVal(ov,null));
				}
			}else if(!nv.equals(ov)){
				if(ov == null && !oldVal.containsKey(k)){
					logVal.put(k,logVal(Log.NOT_EXIST,nv));
				}else {
					logVal.put(k,logVal(ov,nv));
				}
			}
			
		}
		return logVal;
	}
	
	private List<Object> logVal(Object ov,Object nv){
		List<Object> vs = new ArrayList<Object>();
		vs.add(ov);
		vs.add(nv);
		return vs;
	}
	
	protected ObjectField readObjectField(List<ObjectField> fields,String fname){
		for(ObjectField f:fields){
			if(f.getName().equals(fname)){
				return f;
			}
		}
		return null;
	}
	
	/**
	 * 鍏充簬鍐呰仈鐨勨�渁rray.innerObject鈥濈被鍨嬬殑鏇存柊锛�
	 * 绗竴灞俛rray鐨勬洿鏂帮細
	 * 	1. 鏂版暟鎹紝浣跨敤$push鎻掑叆
	 *  2. 鏃ф暟鎹紝閫愭潯閲囩敤$set:array.field鏂瑰紡鏇存柊
	 *  渚嬪瓙锛歅rovince.cities.update([{city:'鏉窞',zipCode:'310000',id:1},{city:'閲戝崕',zipCode:'320000',id:2}])
	 *  鏇存柊浠ｇ爜锛� $set:[{Province.cities.$.city:'鏉窞'},{Province.cities.$.zipCode:'310000'}]
	 *            $query:{id:1}
	 *            $set:[{Province.cities.$.city:'閲戝崕'},{Province.cities.$.zipCode:'320000'}]
	 *            $query:{id:2}
	 * 澶氬眰array鐨勬洿鏂帮細
	 *  1. 浣跨敤$set:parent.arrayField鏂瑰紡鏇存柊
	 *  渚嬪瓙:Province.cities.update({city:'鏉窞',id:1,counties:[{id:11,county:'涓婂煄鍖�'},{id:12,county:'涓嬪煄鍖�'}]})
	 *  鏇存柊浠ｇ爜: $set:[{Province.cities.$.city:'鏉窞'},
	 *  					{Province.cities.$.counties:[{id:11,county:'涓婂煄鍖�'},{id:12,county:'涓嬪煄鍖�'}]
	 *  			   }]
	 *  		  $query:{id:1}
	 * **/
	protected static Map<String,Object> buildUpdates(List<ObjectField> fields,Map<String,Object> innMap,
			String prefix,List<Map<String,BasicDBObject>> updates,BasicDBObject updateQuery,boolean multilevelArray){
		
		Map<String,Object> daoMap = new HashMap<String,Object>();
		String preName = "";
		if(!StringUtil.isEmpty(prefix)){
			preName = prefix+".";
		}
		
		for(ObjectField ff : fields){
			if(!innMap.containsKey(ff.getName()))continue;
			
			boolean mlevel = multilevelArray;
			String key = preName+ff.getName();
			Object value = innMap.get(ff.getName());
			
			if(ff.isInner()){
				if(value != null && value instanceof Map && ((Map)value).size() > 0){
					Map<String,Object> valMap = (Map<String,Object>)value;

					Map<String,Object> m = buildUpdates(ff.getFields(),(Map<String,Object>)value,key,updates,updateQuery,mlevel);
					daoMap.putAll(m);
				}
			}else if(ff.isArray() && (FieldType.INNER_COLLECTION.is(ff.getElementType()) || FieldType.RELATIVE_INNER.is(ff.getElementType()))){
				if(multilevelArray){
					daoMap.put(key, value);
					continue;
				}
				
				if(value != null && value instanceof List && ((List)value).size() > 0){
					List<Map<String,Object>> valList = (List<Map<String,Object>>)value;
					for(Map<String,Object> valMap:valList){
						buildArrayUpdates(valMap,ff,key,updates,updateQuery);
					}
				}else if(value != null && value instanceof Map){
					Map<String,Object> valMap = (Map<String,Object>)value;
					
					if(valMap.size() > 0){
						buildArrayUpdates(valMap,ff,key,updates,updateQuery);
					}
				}
			}else {
				daoMap.put(key, value);
			}
		}
		
		return daoMap;
	}
	
	protected static void buildArrayUpdates(Map<String,Object> valMap,ObjectField innerField,String fieldKey,
			List<Map<String,BasicDBObject>> updates,BasicDBObject updateQuery){
		if(valMap == null)return;
		
		Map<String,Object> updateMap = new HashMap<String,Object>();
		updateMap.putAll(valMap);
		
		if(hasId(updateMap)){
			String id = (String)updateMap.remove("id");
			System.out.println("------ update map id : " + id);
			
			BasicDBObject currQuery = updateQuery;
			if(!Utils.isEmpty(id)){
				currQuery = new BasicDBObject(fieldKey +"._id",new ObjectId(id));
			}

			Map<String,Object> updMap = buildUpdates(innerField.getFields(),updateMap,fieldKey+".$",updates,currQuery,true);
			Map<String,BasicDBObject> dbUpdateMap = buildSetUpdateMap(currQuery,updMap);
			dbUpdateMap.put("value", new BasicDBObject(valMap));
			
			updates.add(dbUpdateMap);
		}else {
			ObjectId newId = new ObjectId();
			valMap.put("id",newId.toString());
			updateMap.put("_id",newId);
			
			Map<String,BasicDBObject> bdbMap = new HashMap<String,BasicDBObject>();
			bdbMap.put("query", updateQuery);
			bdbMap.put("update", new BasicDBObject("$push",new BasicDBObject(fieldKey,updateMap)));
			bdbMap.put("value", new BasicDBObject(valMap));
			
			updates.add(bdbMap);
			
//			dao.getDBCollection("Account").update(new BasicDBObject("personal_profile.address._id",new ObjectId("56dfe161ba594151e4e9ebd6")), 
//					new BasicDBObject("$push",new BasicDBObject(key,valMap)));
		}
	}
	
	protected static Map<String,BasicDBObject> buildSetUpdateMap(BasicDBObject query,Map<String,Object> setMap){
		if(Utils.isEmpty(setMap)){
			return null;
		}
		
		setMap.put("modifiedDate",new Date());
		
		Map<String,BasicDBObject> bdbMap = new HashMap<String,BasicDBObject>();
		bdbMap.put("query", query);
		bdbMap.put("update", new BasicDBObject("$set",setMap));
		
		return bdbMap;
	}
	
	protected static boolean hasId(Object obj){
		if(obj == null)return false;
		
		if(obj instanceof List){
			for(Object oo : (List)obj){
				if(hasId(oo)){
					return true;
				}
			}
		}
		
		if(obj instanceof Map){
			Object id = ((Map)obj).get("id");
			if(id != null && id instanceof ObjectId) {
				return true;
			}
			
			try {
				new ObjectId(id.toString());
				return true;
			}catch(Exception e) {}
			
			for(Object oo : ((Map)obj).values()){
				if(hasId(oo)){
					return true;
				}
			}
		}
		
		return false;
	}
	
	public static void main(String[] args) {
		Map params = new HashMap();
		String s = "addToParams({"
					+ "request:{order:{id:'571734555159bc7e51bfd14c',deliveries:{items:[{product:'56f8f1d98f7725b52a46c548',distributeItems:[{warehouse:'570f11c27a7753755ba47b45',num:'22'}]}]}}},"
					+ "oldOrder:db.Order.get('571734555159bc7e51bfd14c'),"
					+ "oldNum:oldOrder.items.join({"
						+ "right:oldOrder.deliveries.items.group({totalNum:sum(this.distributeItems.sum(this.num)),id:this.product}),"
						+ "on:'left.product=right.id'"
					+ "})"
					+ "newOrder:db.Order.deepConvert(request.order),"
					//+ "id:print('======= id: ' + oldNum)"
				+ "})";
		//AE.execute(s,params);
//		
		s = "oldNum.group({"
				+ "orderNum:this.left.num,"
				+ "deliveryNum:this.right.totalNum,"
				+ "num:this.left.num-if(isNull(this.right.totalNum)){0}else{this.right.totalNum},"
				+ "id:this.left.product.info.name"
			+ "})";
		
		s = "newOrder.deliveries.items.group({num:sum(this.distributeItems.num.sun()),id:this.product.info.name})";
		
		s = "db.Product.get('56f8f1d98f7725b52a46c548')";
//		s = "oldNum.group({"
//				+ "orderNum:this.left.num,"
//				+ "deliveryNum:this.right.totalNum.ifNull(0),"
//				+ "num:orderNum-deliveryNum,"
//				+ "id:this.left.product.info.name"
//			+ "})";
		
//		s = "each(oldNum){{product:this.left.product.info.name,num:this.left.num}}";
		
		s = "db.Account.personal_profile.address.get('56eaa9488f7774696385f2e0')";
		//s = "db.Account.innerObjectById({personal_profile:{address:{id:'56eaa9488f7774696385f2e0'}}})";
		//s = "db.Account.innerObjectById({personal_profile:{address:{id:'56eaa9488f7774696385f2e0'}}})";	
		//Object v = AE.execute(s,null);
		
		s = "{order:{id:'571734555159bc7e51bfd14c', deliveries:{items:[{product:'56f8f1d98f7725b52a46c548', distributeItems:[{num:222, warehouse:'570f11c27a7753755ba47b45'}, {num:22, warehouse:'570f14227a7753755ba47b46'}, {num:11, warehouse:'570f11c27a7753755ba47b45'}, {num:22, warehouse:'570f14227a7753755ba47b46'}, {num:11, warehouse:'570f11c27a7753755ba47b45'}, {num:22, warehouse:'570f14227a7753755ba47b46'}]}, {product:'56f8f2718f7725b52a46c54f', distributeItems:[{num:223, warehouse:'570f11c27a7753755ba47b45'}, {num:33, warehouse:'570f14227a7753755ba47b46'}]}, {product:'570df10e8f77807d6b688d9e', distributeItems:[{num:19, warehouse:'570f11c27a7753755ba47b45'}, {num:321, warehouse:'570f14227a7753755ba47b46'}]}]}}}";
		params = (Map)AE.execute(s,null);
		
		s = "{newOrder:db.Order.deepConvert(order)}";
		Object v = AE.execute(s,params);

		s = "each(newOrder.deliveries.items){{"
				+ "stores:this.product.stores,"
				+ "distributeItems:this.distributeItems"
			+ "}}";
		v = AE.execute(s,(Map)v);
		
		System.out.println(v);
	}
	
	public Map buildInnerGetMap(String coll,String oid){
		if(StringUtil.isEmpty(coll))return null;
		
		String[] colls = coll.split("\\.");
		//String collName = colls[0];
		if(colls.length > 0){
			Map query = null;
			Map child = null;
			for(int i = colls.length-1; i >= 0; i--){
				query = new HashMap();
				
				if(child == null){
					child = new HashMap();
					child.put("id",oid);
				}

				query.put(colls[i],child);
				child = query;
			}
			
			return query;
		}
		
		return null;
	}
	
	/***
	 * 处理Tree模型的专属字段：_parent_, _path_
	 * **/
	private void doTreeModel(Map val,ObjectMetadata metadata){
		if(metadata == null || !ModelType.TREE.is(metadata.getModelType())){
			return;
		}
		
		if(!val.containsKey(ObjectMetadata.TREE_MODEL_PARENT_KEY) && !val.containsKey("parent")) {
			return;
		}
		
		ObjectField field = this.readObjectField(metadata.getFields(),ObjectMetadata.TREE_MODEL_PARENT_KEY);
		String parentCollection = field.getLinkedObject();
		
		Map<String,Object> parent = null;
		Object p = MapHelper.readValue(val,ObjectMetadata.TREE_MODEL_PARENT_KEY);
		if(p == null){
			p = MapHelper.readValue(val,"parent");
			val.remove("parent");
		}
		
		//System.out.println("-------- doTreeModel p : " + p);
		if(p != null){
			if(p instanceof Map){
				parent = (Map<String,Object>)p;
			}else {
				parent = this.getByID(p.toString(), parentCollection);
			}
		}
		
		//System.out.println("-------- doTreeModel parent : " + parent);
		val.put(ObjectMetadata.TREE_MODEL_PARENT_KEY,parent);
		
		List<Object> paths = null;
		if(parent != null){
			paths = (List<Object>)MapHelper.readValue(parent,ObjectMetadata.TREE_MODEL_PATH_KEY);
			if(paths == null){
				paths = new ArrayList<Object>();
			}
			paths.add(parent);
		}
		val.put(ObjectMetadata.TREE_MODEL_PATH_KEY,paths);
	}
	
	private void doModel(Map val,ObjectMetadata metadata){
		if(metadata != null && ModelType.TREE.is(metadata.getModelType())){
			doTreeModel(val,metadata);
		}
	}
	
	/**
	 * 转换成DB模式的关联关系
	 * **/
	public Map toDBCorrelation(Map dataMap,ObjectMetadata metadata){
		return toDBModel(dataMap,metadata.getFields());
	}
	
	public Map toDBCorrelation(Map dataMap,List<ObjectField> fields){
		Map<String,Object> newMap = new HashMap<String,Object>();

		if(dataMap.containsKey("id")){
			Object val = MR.readValue(dataMap,"id");
			MR.setValue(newMap,"id",val == null?null:val.toString());
		}

		if(dataMap.containsKey("_id")){
			MR.setValue(newMap,"_id",MR.readValue(dataMap,"_id"));
		}
		
		for(ObjectField of : fields){
			String fieldName = of.getName();
			if(!dataMap.containsKey(fieldName)) {
				continue;
			}
			
			newMap.put(fieldName,  toDBModel(MR.readValue(dataMap, fieldName),of));
		}
		
		return newMap;
	}
	
	/**
	 * 转换成DB模式的关联关系
	 * **/
	public Object toDBCorrelation(Object fieldValue,ObjectField field){
		if(fieldValue == null) {
			return null;
		}
		
		if(fieldValue instanceof UCValue){
			fieldValue = ((UCValue)fieldValue).getValue();
		}
		
		if(field.isLinkCollection()){
//			if(fieldValue instanceof CallBack){
//				fieldValue = ((CallBack)fieldValue).doCall();
//			}
			
			if(fieldValue instanceof Map){
				fieldValue = readObjectIdToString((Map)fieldValue);
			}
			
			if(fieldValue != null && !(fieldValue instanceof String)){
				fieldValue = fieldValue.toString();
			}
		}
		
		if(FieldType.VARIABLE_LINK.is(field.getType())){
			if(fieldValue instanceof Map){
				String id = readObjectIdToString((Map)fieldValue);
				String mtype = readMetadataType((Map)fieldValue);
				
				if(!StringUtil.isEmpty(id) && !StringUtil.isEmpty(mtype)){
					fieldValue = mtype+","+id;
				}
			}
			
			if(!(fieldValue instanceof String)){
				fieldValue = null;
			}
		}
		
		if(field.isInnerCollection()){
			if(fieldValue instanceof Map){
//				if(((Map) fieldValue).get("id") == null){
//					((Map) fieldValue).put("id",new ObjectId().toString());
//				}
				
				fieldValue = toDBModel((Map)fieldValue,field.getFields());
			}
			
			if(!(fieldValue instanceof Map)){
				fieldValue = null;
			}else {
				((Map)fieldValue).remove(META_KEY);
			}
		}
		
		if(field.isArray()){
			int type = field.getType();

			List temp = new ArrayList();
			fieldValue = Utils.toArray(fieldValue);
			field.setType(field.getElementType());
			if(!Utils.isEmpty(fieldValue)) {
				for(Object arrObj:(Object[])fieldValue){
					/**
					 * 过滤“null”的数据
					 * **/
					if(arrObj == null)continue;
					
					temp.add(toDBModel(arrObj,field));
				}
			}
			
			
			fieldValue = temp;
			field.setType(type);
		}
		
		return fieldValue;
	}

	
	
	/**
	 * 灏嗘暟鎹浆鎹负DB瀛樺偍妯″紡
	 * 1. 'id' (String绫诲瀷) 杞垚'_id'(ObjectId绫诲瀷)
	 * 2. LINK_COLLECTION瀛楁绫诲瀷锛氬�艰嫢涓篗ap锛岃浆鎴恑d(String绫诲瀷)
	 * 3. 鍘绘帀meta鏁版嵁
	 * 4. 鍘绘帀list閲岀殑null鏁版嵁
	 * ***/
	public Map toDBModel(Map dataMap,String collName){
		ObjectMetadata metadata = this.metaDao.getByName(collName);
		return toDBModel(dataMap,metadata.getFields());
	}
	
	public Map toDBModel(Map dataMap,ObjectMetadata metadata){
//		ObjectId id = this.readObjectId(dataMap);
//		dataMap.remove("id");
//		MR.setValue(dataMap,"_id",id);
		
		return toDBModel(dataMap,metadata.getFields());
	}
	
	public Map toDBModel(Map dataMap,List<ObjectField> fields){
		Map<String,Object> newMap = new HashMap<String,Object>();

		if(dataMap.containsKey("id")){
			Object val = MR.readValue(dataMap,"id");
			MR.setValue(newMap,"id",val == null?null:val.toString());
		}

		if(dataMap.containsKey("_id")){
			MR.setValue(newMap,"_id",MR.readValue(dataMap,"_id"));
		}
		
		for(ObjectField of : fields){
			String fieldName = of.getName();
			if(!dataMap.containsKey(fieldName)) {
				continue;
			}
			
			newMap.put(fieldName,  toDBModel(MR.readValue(dataMap, fieldName),of));
		}
		
		return newMap;
	}
	
	
	
	public Object toDBModel(Object fieldValue,ObjectField field){
		if(fieldValue == null) {
			return null;
		}
		
		if(fieldValue instanceof UCValue){
			fieldValue = ((UCValue)fieldValue).getValue();
		}
		
		if(field.isLinkCollection()){
//			if(fieldValue instanceof CallBack){
//				fieldValue = ((CallBack)fieldValue).doCall();
//			}
			
			if(fieldValue instanceof Map){
				fieldValue = readObjectIdToString((Map)fieldValue);
			}
			
			if(fieldValue != null && !(fieldValue instanceof String)){
				fieldValue = fieldValue.toString();
			}
		}
		
		if(FieldType.VARIABLE_LINK.is(field.getType())){
			if(fieldValue instanceof Map){
				String id = readObjectIdToString((Map)fieldValue);
				String mtype = readMetadataType((Map)fieldValue);
				
				if(!StringUtil.isEmpty(id) && !StringUtil.isEmpty(mtype)){
					fieldValue = mtype+","+id;
				}
			}
			
			if(!(fieldValue instanceof String)){
				fieldValue = null;
			}
		}
		
		if(field.isInnerCollection()){
			if(fieldValue instanceof Map){
//				if(((Map) fieldValue).get("id") == null){
//					((Map) fieldValue).put("id",new ObjectId().toString());
//				}
				
				fieldValue = toDBModel((Map)fieldValue,field.getFields());
			}
			
			if(!(fieldValue instanceof Map)){
				fieldValue = null;
			}else {
				((Map)fieldValue).remove(META_KEY);
			}
		}
		
		if(field.isArray()){
			int type = field.getType();

			List temp = new ArrayList();
			fieldValue = Utils.toArray(fieldValue);
			field.setType(field.getElementType());
			if(!Utils.isEmpty(fieldValue)) {
				for(Object arrObj:(Object[])fieldValue){
					/**
					 * 过滤“null”的数据
					 * **/
					if(arrObj == null)continue;
					
					temp.add(toDBModel(arrObj,field));
				}
			}
			
			
			fieldValue = temp;
			field.setType(type);
		}
		
		return fieldValue;
	}
	
	public String readMetadataType(Map dataMap){
		Object from = readMetaValue(dataMap,"from");
		if(!"db".equals(from))return null;
		
		Object type = readMetaValue(dataMap,"type");
		if(type != null){
			return type.toString().trim();
		}
		return null;
	}
	
	public Object readMetaValue(Map dataMap,String name){
		if(dataMap == null)return null;
		
		return MapHelper.readValue(dataMap.get(META_KEY),name);
	}
	
	public ObjectId readObjectId(Map dataMap){
		Object id = MR.readValue(dataMap, "id");
		if(Utils.isEmpty(id)){
			id = MR.readValue(dataMap, "_id");
		}
		
		if(id != null) {
			if(id instanceof ObjectId) {
				return (ObjectId)id;
			}
			try {
				return new ObjectId(id.toString());
			}catch(Exception e) {}
		}
		return null;
	}
	
	public String readObjectIdToString(Map dataMap){
		Object id = this.readObjectId(dataMap);
		if(id == null) {
			return null;
		}
		return id.toString();
	}
	
	/***
	 * 格式化数据：将数据匹配为ObjectMetadata所定义的类型
	 * **/
	public Map<String,Object> formatData(Map<String,Object> dataMap,String collName){
		if(dataMap == null)return null;
		
		ObjectMetadata metadata = this.metaDao.getByName(collName);
		return formatData(dataMap,metadata);
	}
	
	/**
	 * 格式化数据
	 * **/
	public Map<String,Object> formatData(Map<String,Object> dataMap,ObjectMetadata metadata){
		doModel(dataMap,metadata);
		return formatData(dataMap,metadata.getFields());
	}
	
	public Map<String,Object> formatData(Map<String,Object> dataMap,List<ObjectField> fields){
		if(dataMap == null)return null;
		
		for(ObjectField of : fields){
			Object fval = MapHelper.readValue(dataMap,of.getName());
			if(fval == null) {
				continue;
			}
			
			
			
			try {
				dataMap.put(of.getName(), formatFieldValue(of,fval));
			}catch(NullPointerException ne) {
				System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
				System.out.println("--------------- formatData of : " + of.getName());
				System.out.println("--------------- formatData of : " + of.getType());
				ne.printStackTrace();
			}
			
		}
		return dataMap;
	}
	
	public Object formatFieldValue(ObjectField of,Object fieldValue){
		if(fieldValue == null) {
			return null;
		}
		
		if(of.isArray()){
			of.setType(of.getElementType());

			List<Object> result = new ArrayList<Object>();
			if(fieldValue instanceof List){
				for(Object o : (List<Object>)fieldValue){
					result.add(formatFieldValue(of,o));
				}
			}else {
				result.add(formatFieldValue(of,fieldValue));
			}
			
			of.setType(FieldType.ARRAY.getCode());
			return result;
		}else if(of.isInner()){
			if(fieldValue instanceof Map){
				return formatData((Map<String,Object>)fieldValue,of.getFields());
			}
		}else if(of.isLinkCollection()){
			if(fieldValue instanceof Map){
				return formatData((Map<String,Object>)fieldValue,of.getLinkedObject());
			}
			return fieldValue;
		}

		return DaoHelper.correctFieldValue(of.getType(),fieldValue);
	}
	
	public Map<String,Object> readLinkedCollection(Map<String,Object> dataMap,String collName){
		ObjectMetadata metadata = this.metaDao.getByName(collName);
		return this.readLinkedCollection(dataMap, metadata.getFields());
	}
	
	public Map<String,Object> readLinkedCollection(Map<String,Object> dataMap,ObjectMetadata meta){
		return this.readLinkedCollection(dataMap, meta.getFields());
	}
	
	public Map<String,Object> readLinkedCollection(Map<String,Object> dataMap,List<ObjectField> fields){
		if(dataMap == null)return null;
		
		for(ObjectField of : fields){
			String fieldName = of.getName();
			Object fieldValue = dataMap.get(fieldName);
			if(fieldValue == null)continue;
			
			if(of.isLinkCollection()){
				dataMap.put(fieldName, this.delayLinkedObject(fieldValue, of.getLinkedObject()));
			}else if(of.isVariableLink()){
				dataMap.put(fieldName, this.delayVariableLinkedObject(fieldValue));
			}else if(of.isInnerCollection()) {
				if(fieldValue instanceof Map) {
					readLinkedCollection((Map<String,Object>)fieldValue,of.getFields());
				}
			}else if(of.isArray()) {
				fieldValue = Utils.toList(fieldValue);
				dataMap.put(fieldName, fieldValue);
				
				of.setType(of.getElementType());
				
				List<Object> result = new ArrayList<Object>();
				for(Object ov : (List)fieldValue) {
					if(of.isLinkCollection()) {
						result.add(this.delayLinkedObject(ov, of.getLinkedObject()));
						
					}else if(of.isInnerCollection()) {
						if(ov instanceof Map) {
							readLinkedCollection((Map<String,Object>)ov,of.getFields());
						}
					}else if(of.isVariableLink()) {
						result.add(this.delayLinkedObject(ov, of.getLinkedObject()));
					}
				}
				
				if(of.isLinkCollection() || of.isVariableLink()) {
					dataMap.put(fieldName, result);
				}
				
				of.setType(FieldType.ARRAY.getCode());
			}
		}
		return dataMap;
	}
	
	
	
	/***
	 * 格式化数据：将数据转换为ObjectMetadata所定义的类型
	 * **/
	public Map<String,Object> formatData(Map<String,Object> dataMap,String collName,boolean dealWithId,boolean isDeep){
		if(dataMap == null)return null;
		
		ObjectMetadata metadata = this.metaDao.getByName(collName);
		doModel(dataMap,metadata);

		return formatData(dataMap,metadata.getFields(),dealWithId,isDeep);
	}
	
	public Map<String,Object> formatData(Map<String,Object> dataMap,List<ObjectField> fields,boolean toObjectId,boolean isDeep){
		if(dataMap == null)return null;
		
		Map<String,Object> newMap = new HashMap<String,Object>();
		this.fillObjectId(dataMap,newMap,toObjectId);
//		Object id = dataMap.get("id");
//		if(!Utils.isEmpty(id)){
//			if(dealWithId){
//				if(id instanceof ObjectId){
//					newMap.put("_id",id);
//				}else {
//					newMap.put("_id",new ObjectId(id.toString().trim()));
//				}
//			}else {
//				newMap.put("id",id.toString());
//			}
//		}

		for(ObjectField of : fields){
			String fieldName = of.getName();
			Object fieldValue = dataMap.get(fieldName);
			if(fieldValue == null)continue;
			
			newMap.put(fieldName, formatFieldValue(of,fieldValue,toObjectId,isDeep));
		}
		return newMap;
	}
	
	
	
	public Object formatFieldValue(ObjectField of,Object fieldValue,boolean dealWithId,boolean isDeep){
		if(fieldValue == null) {
			return null;
		}
		
		if(FieldType.ARRAY.is(of.getType())){
			of.setType(of.getElementType());

			List<Object> result = new ArrayList<Object>();
			if(fieldValue instanceof List){
				for(Object o : (List<Object>)fieldValue){
					result.add(formatFieldValue(of,o,dealWithId,isDeep));
				}
			}else {
				result.add(formatFieldValue(of,fieldValue,dealWithId,isDeep));
			}
			
			of.setType(FieldType.ARRAY.getCode());
			return result;
		}
		
		if(FieldType.INNER_COLLECTION.is(of.getType())){
			if(fieldValue instanceof Map){
				if(dealWithId){
					Object id = ((Map) fieldValue).get("id");
					try {
						id = new ObjectId(id.toString()).toString();
					}catch(Exception e){
						id = new ObjectId().toString();
					}
					
					((Map) fieldValue).put("id",id);
				}
				
				return formatData((Map<String,Object>)fieldValue,of.getFields(),dealWithId,isDeep);
			}
		}
		
		if(FieldType.LINK_COLLECTION.is(of.getType())){
			if(fieldValue instanceof Map){
				return formatData((Map<String,Object>)fieldValue,of.getLinkedObject(),dealWithId,isDeep);
			}
			if(isDeep){
				return this.delayLinkedObject(fieldValue, of.getLinkedObject());
			}
			return fieldValue;
		}

		return DaoHelper.correctFieldValue(of.getType(),fieldValue);
	}
	
	public Object delayLinkedObject(final Object oid,final String collName){
		if(oid instanceof String && !StringUtil.isEmpty((String)oid)){
			final String linkOid = ((String)oid).trim();
			
			return new CallBack(){
				private Object obj;
				private int num = 0;
				public Object doCall(){
					if(obj == null && num < 3){
						this.obj = getByID(linkOid, collName);
					}
					
					num++;
					return this.obj;
				}
				
				public String toString(){
					return "--------- linkedObject callback : " + linkOid;
				}
			};
		}
		return oid;
	}
	
	public Object getLinkedObject22(Object oid,String collName){
		if(oid instanceof String && !StringUtil.isEmpty((String)oid)){
			String linkOid = ((String)oid).trim();
			return this.getByID(linkOid, collName);
		}
		
		return oid;
	}
	
	public Object delayVariableLinkedObject(Object fval){
		if(fval instanceof String){
			String[] names = ((String)fval).split(",");
			if(names.length == 2){
				String coll = names[0];
				String oid = names[1];
				
				String[] colls = coll.split("\\.");
				String collName = colls[0];
				if(colls.length > 1){
					//return this.getInnerObject(colls[0], colls[1], oid);
					return this.delayInnerObject(colls[0], colls[1], oid);
				}else {
					//return this.getByID(oid, collName);
					return this.delayLinkedObject(oid, collName);
				}
			}
		}
		
		return null;
	}
	
	public Map<String,Object> getByID(String oid,String collName){
		DBCursor cursor = this.getDBCollection(collName).find(this.getObjectId(oid));
		
		while(cursor.hasNext()){
			BasicDBObject doc = (BasicDBObject)cursor.next();
			return toMap(doc,collName);
		}
		
		return this.emptyResult();
	}
	
	public Map<String,Object> emptyResult(){
		return new HashMap<String,Object>();
	}
	
	protected Map<String,Object> toMap(DBObject doc,String collName){
		Map<String,Object> dataMap = doc.toMap();
		mergeLinkedObject(dataMap,collName);
		return dataMap;
	}
	
	protected Map<String,Object> toMap(DBObject doc,String collName,String innerName){
		if(doc == null){
			return null;
		}
		
		ObjectMetadata metedata = this.metaDao.getByName(collName);
		if(metedata == null){
			return null;
		}

		for(ObjectField f : metedata.getFields()){
			if(f.getName().equals(innerName)){
				if(FieldType.ARRAY.is(f.getType()) && FieldType.INNER_COLLECTION.is(f.getElementType())){
					Map<String,Object> dataMap = doc.toMap();

					correctField(dataMap,f.getFields(),rootMeta(collName+"."+innerName));
					return dataMap;
				}
			}
		}

		return null;
	}
	
	protected void mergeLinkedObject(Map<String,Object> dataMap,String collName){
		ObjectMetadata metedata = this.metaDao.getByName(collName);
		if(metedata == null){
			return;
		}
		
		correctField(dataMap,metedata.getFields(),rootMeta(collName));
	}
	
	private void fillObjectId(Map<String,Object> dataMap,Map<String,Object> newMap,boolean toObjectId) {
		Object id = dataMap.get("id");
		if(!Utils.isEmpty(id)){
			if(toObjectId){
				if(id instanceof ObjectId){
					newMap.put("_id",id);
				}else {
					newMap.put("_id",new ObjectId(id.toString().trim()));
				}
			}else {
				newMap.put("id",id.toString());
			}
		}
	}

	protected void correctField(Map<String,Object> dataMap,List<ObjectField> fields,Map meta){
		objectIdToString(dataMap);
		dataMap.put(META_KEY, meta);

		for(ObjectField field : fields){
			String fieldName = field.getName();
			Object fieldValue = dataMap.get(fieldName);
			
			if(FieldType.LINK_COLLECTION.is(field.getType())){
				dataMap.put(fieldName, delayLinkedObject(fieldValue,field.getLinkedObject()));
			}else if(FieldType.VARIABLE_LINK.is(field.getType())){
				dataMap.put(fieldName, this.delayVariableLinkedObject(fieldValue));
			}else if(field.isInner()){
				if(fieldValue instanceof DBObject){
					fieldValue = ((DBObject)fieldValue).toMap();
					dataMap.put(fieldName, fieldValue);
					//correctField((Map<String,Object>)fieldValue,field.getFields(),meta(meta,field));
				}
				
				if(fieldValue instanceof Map){
					correctField((Map<String,Object>)fieldValue,field.getFields(),meta(meta,field));
				}
			}else if(FieldType.ARRAY.is(field.getType())){
				if(fieldValue instanceof List){
					List list = new ArrayList();
					for(Object obj : (List)fieldValue){
						if(FieldType.INNER_COLLECTION.is(field.getElementType()) || FieldType.RELATIVE_INNER.is(field.getElementType())){
							if(obj instanceof DBObject){
								obj = ((DBObject)obj).toMap();
								//correctField((Map<String,Object>)fieldValue,field.getFields(),meta(meta,field));
							}
							
							if(obj instanceof Map){
								correctField((Map)obj,field.getFields(),meta(meta,field));
							}

							
							list.add(obj);
						}else if(FieldType.LINK_COLLECTION.is(field.getElementType())){
							list.add(delayLinkedObject(obj,field.getLinkedObject()));
						}else if(FieldType.VARIABLE_LINK.is(field.getType())){
							list.add(this.delayVariableLinkedObject(fieldValue));
						}else {
							list = (List)fieldValue;
						}
						
						dataMap.put(fieldName, list);
					}
					
					/*
					for(int i = 0; i < list.size(); i++){
						Object obj = list.get(i);
						
						if(FieldType.INNER_COLLECTION.is(field.getElementType())){
							if(obj instanceof DBObject){
								obj = ((DBObject)obj).toMap();
								//correctField((Map<String,Object>)fieldValue,field.getFields(),meta(meta,field));
							}
							
							if(obj instanceof Map){
								correctField((Map)obj,field.getFields(),meta(meta,field));
							}

							list.set(i,obj);
						}else if(FieldType.LINK_COLLECTION.is(field.getElementType())){
							list.set(i, getLinkedObject(obj,field.getLinkedObject()));
						}else if(FieldType.VARIABLE_LINK.is(field.getType())){
							list.set(i, this.getVariableLinkedObject(fieldValue));
						}
					}*/
				}
			}else if(field.getType() >= 100){
				if(fieldValue != null){
					UCType type = this.uctypeDao.getByKey(field.getType());
					UCValue val = new UCValue();
					val.setValue(fieldValue.toString());
					val.setType(type);
					
					fieldValue = val;
					dataMap.put(fieldName, fieldValue);
				}
			}
		}
	}
	
	public Map rootMeta(String collName){
		Map meta = new HashMap();
		meta.put("type", collName);
		meta.put("from","db");
		
		return meta;
	}
	
	public Map meta(Map parent,ObjectField field){
		Map meta = new HashMap();
		meta.put("from","db");
		
		Object pt = MapHelper.readValue(parent,"type");
		String pre = pt == null?"":pt+".";
		meta.put("type", pre+field.getName());
		
		return meta;
	}

	private void objectIdToString(Map<String,Object> dataMap){
		if(dataMap.get("_id") != null){
			Object id = dataMap.remove("_id");
			if(id instanceof ObjectId){
				id = id.toString();
			}
			dataMap.put("id",id);
		}
	}
}
