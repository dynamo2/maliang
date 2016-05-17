package com.maliang.core.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import com.maliang.core.arithmetic.AE;
import com.maliang.core.model.FieldType;
import com.maliang.core.model.ObjectField;
import com.maliang.core.model.ObjectMetadata;
import com.maliang.core.service.MapHelper;
import com.maliang.core.util.StringUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class BasicDao extends AbstractDao{
	private static String OBJECT_METADATA_KEY = "_object_metadata_";
	protected ObjectMetadataDao metaDao = new ObjectMetadataDao();
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
								returnObject.put("_RootObject_", rootObject);
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
	
	protected static Map<String,Object> buildUpdates(List<ObjectField> fields,Map<String,Object> innMap,
			String prefix,List<Map<String,BasicDBObject>> updates,BasicDBObject updateQuery){
		
		Map<String,Object> daoMap = new HashMap<String,Object>();
		String preName = "";
		if(prefix != null && prefix.trim().length() > 0){
			preName = prefix+".";
		}
		
		for(ObjectField ff : fields){
			if(!innMap.containsKey(ff.getName()))continue;
			
			String key = preName+ff.getName();
			Object value = innMap.get(ff.getName());
			
			if(FieldType.INNER_COLLECTION.is(ff.getType())){
				if(value != null && value instanceof Map && ((Map)value).size() > 0){
					Map<String,Object> valMap = (Map<String,Object>)value;

					Map<String,Object> m = buildUpdates(ff.getFields(),(Map<String,Object>)value,key,updates,updateQuery);
					daoMap.putAll(m);
				}
			}else if(FieldType.ARRAY.is(ff.getType()) && FieldType.INNER_COLLECTION.is(ff.getElementType())){
				if(value != null && value instanceof List && ((List)value).size() > 0){
					List<Map<String,Object>> valList = (List<Map<String,Object>>)value;
					for(Map<String,Object> valMap:valList){
						buildInnerUpdates(valMap,ff,key,updates,updateQuery);
					}
				}else if(value != null && value instanceof Map){
					Map<String,Object> valMap = (Map<String,Object>)value;
					
					if(valMap.size() > 0){
						buildInnerUpdates(valMap,ff,key,updates,updateQuery);
					}
				}
			}else {
				daoMap.put(key, value);
			}
		}
		
		return daoMap;
	}
	
	protected static void buildInnerUpdates(Map<String,Object> valMap,ObjectField innerField,String fieldKey,
			List<Map<String,BasicDBObject>> updates,BasicDBObject updateQuery){
		if(hasId(valMap)){
			String id = (String)valMap.remove("id");
			BasicDBObject currQuery = updateQuery;
			if(id != null){
				currQuery = new BasicDBObject(fieldKey +"._id",new ObjectId(id));
			}

			Map<String,Object> updMap = buildUpdates(innerField.getFields(),valMap,fieldKey+".$",updates,currQuery);
			updates.add(buildSetUpdateMap(currQuery,updMap));
		}else {
			valMap.put("_id",new ObjectId());
			
			Map<String,BasicDBObject> bdbMap = new HashMap<String,BasicDBObject>();
			bdbMap.put("query", updateQuery);
			bdbMap.put("update", new BasicDBObject("$push",new BasicDBObject(fieldKey,valMap)));
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
		AE.execute(s,params);
//		
		s = "oldNum.group({"
				+ "orderNum:this.left.num,"
				+ "deliveryNum:this.right.totalNum,"
				+ "num:this.left.num-if(isNull(this.right.totalNum)){0}else{this.right.totalNum},"
				+ "id:this.left.product.info.name"
			+ "})";
		
		s = "newOrder.deliveries.items.group({num:sum(this.distributeItems.num.sun()),id:this.product.info.name})";
		
//		s = "oldNum.group({"
//				+ "orderNum:this.left.num,"
//				+ "deliveryNum:this.right.totalNum.ifNull(0),"
//				+ "num:orderNum-deliveryNum,"
//				+ "id:this.left.product.info.name"
//			+ "})";
		
//		s = "each(oldNum){{product:this.left.product.info.name,num:this.left.num}}";
		Object v = AE.execute(s,params);
		
		System.out.println(v);
	}

	public Map<String,Object> correctData(Map<String,Object> dataMap,String collName,boolean dealWithId,boolean isDeep){
		if(dataMap == null)return null;
		
		ObjectMetadata metadata = this.metaDao.getByName(collName);
		return correctData(dataMap,metadata.getFields(),dealWithId,isDeep);
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
			if(fieldValue instanceof List){
				List<Object> result = new ArrayList<Object>();
				for(Object o : (List<Object>)fieldValue){
					of.setType(of.getElementType());
					result.add(correctFieldValue(of,o,dealWithId,isDeep));
				}
				
				return result;
			}else if(fieldValue instanceof Map){
				of.setType(of.getElementType());
				
				List<Object> result = new ArrayList<Object>();
				result.add(correctFieldValue(of,fieldValue,dealWithId,isDeep));
				return result;
			}else {
				return null;
			}
		}
		
		if(FieldType.INNER_COLLECTION.is(of.getType())){
			if(fieldValue instanceof Map){
				if(dealWithId){
					if(((Map) fieldValue).get("id") == null){
						((Map) fieldValue).put("id",new ObjectId().toString());
					}
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
	
	private void mergeLinkedObject(Map<String,Object> dataMap,String collName){
		ObjectMetadata metedata = this.metaDao.getByName(collName);
		if(metedata == null){
			return;
		}
		
		correctField(dataMap,metedata.getFields());
	}

	private void correctField(Map<String,Object> dataMap,List<ObjectField> fields){
		objectIdToString(dataMap);

		for(ObjectField field : fields){
			String fieldName = field.getName();
			Object fieldValue = dataMap.get(fieldName);
			
			if(FieldType.LINK_COLLECTION.is(field.getType())){
				dataMap.put(fieldName, getLinkedObject(fieldValue,field.getLinkedObject()));
			}else if(FieldType.INNER_COLLECTION.is(field.getType())){
				if(fieldValue instanceof Map){
					correctField((Map<String,Object>)fieldValue,field.getFields());
				}
			}else if(FieldType.ARRAY.is(field.getType())){
				if(fieldValue instanceof List){
					List list = (List)fieldValue;
					for(int i = 0; i < list.size(); i++){
						Object obj = list.get(i);
						
						if(FieldType.INNER_COLLECTION.is(field.getElementType())){
							if(obj instanceof Map){
								correctField((Map)obj,field.getFields());
							}
						}else if(FieldType.LINK_COLLECTION.is(field.getElementType())){
							list.set(i, getLinkedObject(obj,field.getLinkedObject()));
						}
					}
				}
			}
		}
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
