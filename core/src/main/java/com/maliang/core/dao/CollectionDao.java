package com.maliang.core.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.maliang.core.model.FieldType;
import com.maliang.core.model.ObjectField;
import com.maliang.core.model.ObjectMetadata;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class CollectionDao extends BasicDao {
	public void save(Map value,String collName) {
		BasicDBObject doc = new BasicDBObject(value);
		this.getDBCollection(collName).save(doc);
		
		value.put("id", doc.getObjectId("_id").toByteArray());
	}
	
	public Map<String,Object> getByID(String oid,String collName){
		DBCursor cursor = this.getDBCollection(collName).find(this.getObjectId(oid));
		
		while(cursor.hasNext()){
			BasicDBObject doc = (BasicDBObject)cursor.next();
			
			Map<String,Object> map = doc.toMap();
			mergeLinkedObject(map,collName);
			
			return map;
		}
		
		return null;
	}
	
	public List<Map<String,Object>> find(BasicDBObject query,String collName){
		DBCursor cursor = this.getDBCollection(collName).find(query);
		
		List<Map<String,Object>> results = new ArrayList<Map<String,Object>>();
		for(DBObject dob : cursor.toArray()){
			Map<String,Object> dataMap = dob.toMap();
			mergeLinkedObject(dataMap,collName);
			
			results.add(dataMap);
		}
		
		return results;
	}
	
	public void remove(String oid,String collName){
		this.getDBCollection(collName).remove(this.getObjectId(oid));
	}
	
	private void mergeLinkedObject(Map<String,Object> dataMap,String collName){
		dataMap.put("id",dataMap.get("_id").toString());
		
		ObjectMetadata metedata = this.metaDao.getByName(collName);
		for(ObjectField of : metedata.getFields()){
			String fieldName = of.getName();
			if(of.getType() == FieldType.LINK_COLLECTION.getCode()){
				String linkCollName = getLinkedCollectionName(of.getLinkedObject());
				if(linkCollName == null)continue;
				
				Object fieldValue = dataMap.get(fieldName);
				if(fieldValue != null && fieldValue instanceof String && !((String)fieldValue).trim().isEmpty()){
					String linkOid = ((String)fieldValue).trim();
					fieldValue = this.getByID(linkOid, linkCollName);
				}
				dataMap.put(fieldName, fieldValue);
			}
		}
	}
	
	private String getLinkedCollectionName(String linkedObjectId){
		ObjectMetadata linkMeta = this.metaDao.getByID(linkedObjectId);
		if(linkMeta != null){
			return linkMeta.getName();
		}
		return null;
	}
	
	private void correct(Map value){
		BasicDBObject d = null;
		d.toMap();
	}
	
	public static void main(String[] args) {
		CollectionDao dao = new CollectionDao();
		
//		Map m = new HashMap();
//		m.put("name", "雪花秀");
//		
//		//dao.save(m, "Brand");
//		
//		Map bdc = dao.getByID("5562fd11bd77137b45adcb44", "Brand");
//		System.out.println(bdc);
//		
//		m = new HashMap();
//		m.put("name", "珍雪面霜60ml");
//		m.put("brand", "5562fd11bd77137b45adcb44");
//		m.put("price", "2285.00");
//		
//		//dao.save(m, "Product");
//		bdc = dao.getByID("5563e32cbd779fb4ab91984c", "Product");
//		System.out.println(bdc);


		dao.remove("55648ebdbd77bd914c6194b6", "Product");
		dao.remove("556490abbd77524954cecd70", "Product");
		
		List<Map<String,Object>> ps = dao.find(null, "Product");
		
		System.out.println(ps);
	}
}
