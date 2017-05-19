package com.maliang.core.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import com.maliang.core.arithmetic.AE;
import com.maliang.core.model.FieldType;
import com.maliang.core.model.ObjectField;
import com.maliang.core.model.ObjectMetadata;
import com.maliang.core.model.Project;
import com.maliang.core.model.UCType;
import com.maliang.core.model.UCValue;
import com.maliang.core.service.MapHelper;
import com.maliang.core.util.StringUtil;
import com.maliang.core.util.Utils;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class BasicDao extends AbstractDao{
	private static String META_KEY = ".meta";
	
	protected ObjectMetadataDao metaDao = new ObjectMetadataDao();
	protected UCTypeDao uctypeDao = new UCTypeDao();
	//protected CollectionDao collDao = new CollectionDao();
	public static final String[] DB_KEYWORDS = {"if","then","else"};

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
	
	public void save(BasicDBObject doc,String collName) {
		this.getDBCollection(collName).save(doc);
	}
	
	
	public Map<String,Object> getInnerObject(String collName,String innerName,String oid){
		List<DBObject> pipe = new ArrayList<DBObject>();
		
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
			results.add(toMap(dob,collName));
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

	protected BasicDBObject build(Map<String,Object> query){
		if(query == null || query.isEmpty()){
			return null;
		}
		
		return new BasicDBObject(query);
	}
	
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
	
	/**
	 * 关于内联的“array.innerObject”类型的更新：
	 * 第一层array的更新：
	 * 	1. 新数据，使用$push插入
	 *  2. 旧数据，逐条采用$set:array.field方式更新
	 *  例子：Province.cities.update([{city:'杭州',zipCode:'310000',id:1},{city:'金华',zipCode:'320000',id:2}])
	 *  更新代码： $set:[{Province.cities.$.city:'杭州'},{Province.cities.$.zipCode:'310000'}]
	 *            $query:{id:1}
	 *            $set:[{Province.cities.$.city:'金华'},{Province.cities.$.zipCode:'320000'}]
	 *            $query:{id:2}
	 * 多层array的更新：
	 *  1. 使用$set:parent.arrayField方式更新
	 *  例子:Province.cities.update({city:'杭州',id:1,counties:[{id:11,county:'上城区'},{id:12,county:'下城区'}]})
	 *  更新代码: $set:[{Province.cities.$.city:'杭州'},
	 *  					{Province.cities.$.counties:[{id:11,county:'上城区'},{id:12,county:'下城区'}]
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
			
			if(FieldType.INNER_COLLECTION.is(ff.getType())){
				if(value != null && value instanceof Map && ((Map)value).size() > 0){
					Map<String,Object> valMap = (Map<String,Object>)value;

					Map<String,Object> m = buildUpdates(ff.getFields(),(Map<String,Object>)value,key,updates,updateQuery,mlevel);
					daoMap.putAll(m);
				}
			}else if(FieldType.ARRAY.is(ff.getType()) && FieldType.INNER_COLLECTION.is(ff.getElementType())){
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
			BasicDBObject currQuery = updateQuery;
			if(id != null){
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
		if(setMap == null || setMap.size() == 0)return null;
		
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
			if(((Map)obj).get("id") != null){
				return true;
			}
			
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

	public Map<String,Object> correctData(Map<String,Object> dataMap,String collName,boolean dealWithId,boolean isDeep){
		if(dataMap == null)return null;
		
		ObjectMetadata metadata = this.metaDao.getByName(collName);
		return correctData(dataMap,metadata.getFields(),dealWithId,isDeep);
	}
	
	/**
	 * 将数据转换为DB存储模式
	 * 1. 'id' (String类型) 转成'_id'(ObjectId类型)
	 * 2. LINK_COLLECTION字段类型：值若为Map，转成id(String类型)
	 * 3. 去掉meta数据
	 * 4. 去掉list里的null数据
	 * ***/
	public Map toDBModel(Map dataMap,String collName){
		ObjectMetadata metadata = this.metaDao.getByName(collName);
		return toDBModel(dataMap,metadata.getFields());
	}
	
	public Map toDBModel(Map dataMap,List<ObjectField> fields){
		Map<String,Object> newMap = new HashMap<String,Object>();
		
		if(!StringUtil.isEmpty((String)dataMap.get("id"))){
			//newMap.put("_id",new ObjectId(dataMap.get("id").toString().trim()));
			newMap.put("id",dataMap.get("id"));
		}
		
		if(dataMap.get("_id") != null){
			newMap.put("_id",dataMap.get("_id"));
		}
		
		if(!newMap.containsKey("id") || !newMap.containsKey("_id")){
			//newMap.put("_id",new ObjectId());
		}
		
		for(ObjectField of : fields){
			String fieldName = of.getName();
			Object fieldValue = dataMap.get(fieldName);
			if(fieldValue == null)continue;

			newMap.put(fieldName, toDBModel(fieldValue,of));
		}
		
		return newMap;
	}
	
	public Object toDBModel(Object fieldValue,ObjectField field){
		if(fieldValue instanceof UCValue){
			fieldValue = ((UCValue)fieldValue).getValue();
		}
		
		if(FieldType.LINK_COLLECTION.is(field.getType())){
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
		
		if(FieldType.INNER_COLLECTION.is(field.getType())){
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
		
		if(FieldType.ARRAY.is(field.getType())){
			int type = field.getType();
			
			if(Utils.isArray(fieldValue)){
				List newList = new ArrayList();
				field.setType(field.getElementType());
				for(Object arrObj:Utils.toArray(fieldValue)){
					if(arrObj == null)continue;
					
					newList.add(toDBModel(arrObj,field));
				}
				
				fieldValue = newList;
			}
			
			if(!Utils.isArray(fieldValue)){
				fieldValue = null;
			}
			
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
	
	public String readObjectIdToString(Map dataMap){
		String id = StringUtil.getNotEmptyValue(dataMap,"id");
		if(id == null){
			id = StringUtil.getNotEmptyValue(dataMap,"_id");
		}

		if(id != null){
			return id.trim();
		}
		
		return null;
	}
	
	public Map<String,Object> correctData(Map<String,Object> dataMap,List<ObjectField> fields,boolean dealWithId,boolean isDeep){
		if(dataMap == null)return null;
		
		Map<String,Object> newMap = new HashMap<String,Object>();
		
		if(!StringUtil.isEmpty((String)dataMap.get("id"))){
			if(dealWithId){
				newMap.put("_id",new ObjectId(dataMap.get("id").toString().trim()));
			}else {
				newMap.put("id",dataMap.get("id"));
			}
		}

		for(ObjectField of : fields){
			String fieldName = of.getName();
			Object fieldValue = dataMap.get(fieldName);
			if(fieldValue == null)continue;
			
			newMap.put(fieldName, correctFieldValue(of,fieldValue,dealWithId,isDeep));
		}
		return newMap;
	}
	
	public Object correctFieldValue(ObjectField of,Object fieldValue,boolean dealWithId,boolean isDeep){
		if(FieldType.ARRAY.is(of.getType())){
			of.setType(of.getElementType());
			
			Object result = null;
			if(fieldValue instanceof List){
				List<Object> list = new ArrayList<Object>();
				for(Object o : (List<Object>)fieldValue){
					list.add(correctFieldValue(of,o,dealWithId,isDeep));
				}
				result = list;
			}else if(fieldValue instanceof Map){
				List<Object> list = new ArrayList<Object>();
				list.add(correctFieldValue(of,fieldValue,dealWithId,isDeep));
				result = list;
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
				
				return correctData((Map<String,Object>)fieldValue,of.getFields(),dealWithId,isDeep);
			}
		}
		
		if(FieldType.LINK_COLLECTION.is(of.getType())){
			if(fieldValue instanceof Map){
				return correctData((Map<String,Object>)fieldValue,of.getLinkedObject(),dealWithId,isDeep);
			}
			
			if(isDeep){
				return this.getLinkedObject(fieldValue, of.getLinkedObject());
			}
			
			return fieldValue;
		}

		return DaoHelper.correctFieldValue(of.getType(),fieldValue);
	}
	
	public Object getLinkedObject(Object oid,String collName){
		if(oid instanceof String && !StringUtil.isEmpty((String)oid)){
			String linkOid = ((String)oid).trim();
			return this.getByID(linkOid, collName);
		}
		
		return oid;
	}
	
	public Object getVariableLinkedObject(Object fval){
		if(fval instanceof String){
			String[] names = ((String)fval).split(",");
			if(names.length == 2){
				String coll = names[0];
				String oid = names[1];
				
				String[] colls = coll.split("\\.");
				String collName = colls[0];
				if(colls.length > 1){
					return this.getInnerObject(colls[0], colls[1], oid);
				}else {
					return this.getByID(oid, collName);
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

	protected void correctField(Map<String,Object> dataMap,List<ObjectField> fields,Map meta){
		objectIdToString(dataMap);
		dataMap.put(META_KEY, meta);

		for(ObjectField field : fields){
			String fieldName = field.getName();
			Object fieldValue = dataMap.get(fieldName);
			
			if(FieldType.LINK_COLLECTION.is(field.getType())){
				dataMap.put(fieldName, getLinkedObject(fieldValue,field.getLinkedObject()));
			}else if(FieldType.VARIABLE_LINK.is(field.getType())){
				dataMap.put(fieldName, this.getVariableLinkedObject(fieldValue));
			}else if(FieldType.INNER_COLLECTION.is(field.getType())){
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
					List list = (List)fieldValue;
					
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
					}
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
