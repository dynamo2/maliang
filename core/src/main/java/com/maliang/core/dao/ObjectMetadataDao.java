package com.maliang.core.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import com.maliang.core.arithmetic.AE;
import com.maliang.core.model.ObjectField;
import com.maliang.core.model.ObjectMetadata;
import com.maliang.core.model.Project;
import com.maliang.core.model.Trigger;
import com.maliang.core.model.TriggerAction;
import com.mongodb.BasicDBObject;

public class ObjectMetadataDao  extends ModelDao<ObjectMetadata> {
	
	protected static String COLLECTION_NAME = "ObjectMetadata";
	
	static {
		INNER_TYPE.put("ObjectMetadata.fields",ObjectField.class);
		INNER_TYPE.put("ObjectMetadata.triggers",Trigger.class);
		INNER_TYPE.put("ObjectField.fields",ObjectField.class);
		INNER_TYPE.put("Trigger.actions",TriggerAction.class);
	}
	
	protected TreeModelDao treeDao = new TreeModelDao();
	
	public ObjectMetadataDao(){
		super(COLLECTION_NAME,ObjectMetadata.class);
	}

	public ObjectMetadata getByUniqueMark(String mark){
		return this.getByField("unique_mark", mark);
	}
	
	public void removeModelTypeFields(ObjectMetadata meta){
		this.treeDao.removeTreeFields(meta);
	}
	
	public ObjectMetadata getByName(String name){
		Project project = null;
		if(!this.isSystemCollection(name)){
			project = getSessionProject();
		}
		
		return this.getByName(name, project);
	}
	
	public ObjectMetadata getByName(String name,Project project){
		Map query = new HashMap();
		query.put("name", name);
		
		if(project != null){
			query.put("project", project.getId().toString());
		}
		return this.findOne(new BasicDBObject(query));
	}
	
	public ObjectMetadata findOne(BasicDBObject query){
		ObjectMetadata meta = super.findOne(query);
		
		if(treeDao.isTreeModel(meta)){
			List<ObjectField> tFields = treeDao.treeFields(meta.getName());
			if(meta.getFields() == null){
				meta.setFields(tFields);
			}else {
				meta.getFields().addAll(tFields);
			}
		}
		
		return meta;
	}

	public void saveFields(String oid,ObjectField field) {
		if(this.isDuplicateMark(oid, field)){
			throw new RuntimeException("Is duplicate field.name");
		}
		
		if(field.getId() == null) {
			field.setId(new ObjectId());
			
			BasicDBObject doc = encode(field,true);
			this.dbColl.update(this.getObjectId(oid), new BasicDBObject("$push",new BasicDBObject("fields",doc)));
		}else {
			BasicDBObject doc = encode(field,true);
			this.dbColl.update(new BasicDBObject("fields._id",field.getId()), new BasicDBObject("$set",new BasicDBObject("fields.$",doc)));
		}
	}
	
	public Trigger getTriggerById(String tid){
		try {
			return this.getArrayInnerById("triggers", new ObjectId(tid),Trigger.class);
		}catch(Exception e){
			return null;
		}
	}
	
	/**
	 * omId: ObjectMetadata.id
	 * **/
	public void saveTrigger(String omId,Trigger trigger){
		this.saveArrayInnerFields(omId, "triggers", trigger);
	}

	/**
	 * 鍒ゆ柇fields.unique_mark鏄惁鏈夐噸澶嶇殑鍊�
	 * **/
	protected boolean isDuplicateMark(String oid,ObjectField field){
		return isDuplicate(oid,"fields","unique_mark",field.getUniqueMark(),field);
	}

	private void testSave(){
		List<ObjectMetadata> metadataList = this.list();
		ObjectMetadata om = this.getByID("56cfb7b7e45900ecf9b71d4a");
		
		
		System.out.println("============= om =================");
		System.out.println(om);
		
		om.setName(om.getName()+"_1");
		System.out.println(om);
		
		this.save(om);
		om = this.getByID("56cfb7b7e45900ecf9b71d4a");
		System.out.println(om);
		
		metadataList = this.list();
		System.out.println(metadataList.get(0));
	}

	private void testFind(){
		//BasicDBObject doc = (BasicDBObject)this.dbColl.find(new BasicDBObject("id", 10)).toArray().get(0);
		//BasicDBObject doc = (BasicDBObject)this.dbColl.find(this.getObjectId("54c9e4b6fc778a8689446ce7")).toArray().get(0);

		//BasicDBObject bc = (BasicDBObject)this.dbColl.find(new BasicDBObject("fields.unique_mark","user")).toArray().get(0);
		//System.out.println(bc);

		System.out.println(this.getByID("54c9e4b6fc778a8689446ce7"));
		ObjectField of = new ObjectField();
		//of.setId(new ObjectId("54d0371d50f1324ab162ba7a"));
		of.setName("zerofdsa345");
		of.setUniqueMark("322100");
		
		//this.saveFields("54c9e4b6fc778a8689446ce7", of);
		
		//System.out.println(this.getByID("54c9e4b6fc778a8689446ce7"));
		
		System.out.println(isDuplicateMark("54c9e4b6fc778a8689446ce7",of));
	}
	
	public static void main(String[] args) {
		
		ObjectMetadataDao dao = new ObjectMetadataDao();
		
		
		ObjectMetadata od = dao.getByName("Product");
		
		String s = "pdb.showAll()";
		Object val = AE.execute(s);
		
		System.out.println("================");
		System.out.println("val : " + val);
		
		
	}
	
	/**
	public void save(ObjectMetadata om) {
		BasicDBObject doc = encode(om);
		this.dbColl.save(doc);
		
		if(om.getId() == null){
			om.setId(doc.getObjectId("_id"));
		}
	}
	
	public ObjectMetadata getByID(String oid){
		DBCursor cursor = this.dbColl.find(this.getObjectId(oid));
		while(cursor.hasNext()){
			return this.decode((BasicDBObject)cursor.next(), ObjectMetadata.class) ;
		}
		
		return null;
	}
	
	public void remove(String oid){
		this.dbColl.remove(this.getObjectId(oid));
	}
	
	
	public List<ObjectMetadata> list(){
		DBCursor cursor = this.dbColl.find();
		
		return readCursor(cursor,ObjectMetadata.class);
	}
	
	public ObjectMetadata getByUniqueMark(String mark){
		DBCursor cursor = this.dbColl.find(new BasicDBObject("unique_mark",mark));
		while(cursor.hasNext()){
			return this.decode((BasicDBObject)cursor.next(), ObjectMetadata.class);
		}
		
		return null;
	}
	
	public ObjectMetadata getByName(String name){
		DBCursor cursor = this.dbColl.find(new BasicDBObject("name",name));
		while(cursor.hasNext()){
			return this.decode((BasicDBObject)cursor.next(), ObjectMetadata.class);
		}
		
		return null;
	}
	
	public List<DBObject> find(BasicDBObject query){
		DBCursor cursor = this.dbColl.find(query);
		return cursor.toArray();
	}
	
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
	*/
}
