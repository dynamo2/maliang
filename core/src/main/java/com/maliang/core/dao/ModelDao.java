package com.maliang.core.dao;

import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import com.maliang.core.model.MongodbModel;
import com.maliang.core.model.ObjectField;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class ModelDao<T extends MongodbModel> extends AbstractDao {
	protected final DBCollection dbColl;
	protected final Class<T> modelClass;
	
	protected ModelDao(String collName,Class<T> c){
		dbColl = this.getDBCollection(collName);
		this.modelClass = c;
	}
	
	public void save(T om) {
		BasicDBObject doc = encode(om);

		System.out.println("------- save doc : " + doc);
		
		if(om.getId() != null){
			this.dbColl.update(
				new BasicDBObject("_id",om.getId()),new BasicDBObject("$set", doc));
		}else {
			this.dbColl.save(doc);
		}
		
		if(om.getId() == null){
			om.setId(doc.getObjectId("_id"));
		}
	}
	
	public T getByID(String oid){
		return this.findOne(this.getObjectId(oid));
	}
	
	public T getByField(String field,Object value){
		return this.findOne(new BasicDBObject(field,value));
	}
	
	public T findOne(BasicDBObject query){
		DBCursor cursor = this.dbColl.find(query);
		while(cursor.hasNext()){
			return this.decode((BasicDBObject)cursor.next(), this.modelClass);
		}
		
		return null;
	}
	
	public void remove(String oid){
		this.dbColl.remove(this.getObjectId(oid));
	}
	
	public List<DBObject> find(BasicDBObject query){
		DBCursor cursor = this.dbColl.find(query);
		return cursor.toArray();
	}
	
	public List<T> list(){
		return readCursor(this.dbColl.find(),this.modelClass);
	}
	
	@SuppressWarnings("rawtypes")
	public List<T> list(Map query){
		BasicDBObject dbQuery = new BasicDBObject(query);
		return readCursor(this.dbColl.find(dbQuery),this.modelClass);
	}
	
	/**
	 * 判断parentKey.fieldKey是否有重复的值
	 * 如：ObjectMetadata的fields.unique_mark
	 * **/
	protected boolean isDuplicate(String oid,String parentKey,String fieldKey,String fieldValue,MongodbModel model){
		BasicDBObject doc = new BasicDBObject("_id",new ObjectId(oid));
		doc.put(parentKey+"."+fieldKey, fieldValue);
		
		BasicDBObject incKey = new BasicDBObject(parentKey+".$",1);
		List<DBObject> results = this.dbColl.find(doc,incKey).toArray();
		
		if(results.size() == 0)return false;
		
		BasicDBObject result = (BasicDBObject)results.get(0);
		if(!result.containsField(parentKey))return false;
		
		List<BasicDBObject> fields = (List<BasicDBObject>)result.get(parentKey);
		if(fields == null || fields.size() == 0){
			return false;
		}
		
		ObjectField oldField = this.decode(fields.get(0), ObjectField.class);
		if(oldField.getId().equals(model.getId())) {
			return false;
		}
		return oldField.getName().equalsIgnoreCase(fieldValue);
	}
}
