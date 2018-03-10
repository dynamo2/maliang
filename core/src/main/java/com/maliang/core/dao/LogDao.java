package com.maliang.core.dao;

import java.util.Map;

import org.bson.types.ObjectId;

import com.maliang.core.model.Log;
import com.mongodb.BasicDBObject;

public class LogDao extends ModelDao<Log> {
	protected static String COLLECTION_NAME = "Log";
	
	public static void main(String[] args) {
		LogDao dao = new LogDao();
		Log log = dao.getByID("5a123207d097f289ecfc99d9");
		
		System.out.println(" old : " + log.oldValue());
		System.out.println(" new : " + log.newValue());
	}
	
	public LogDao(){
		super(COLLECTION_NAME,Log.class);
	}
	
	public void insertLog(BasicDBObject doc,String collName){
		Log log = new Log();
		log.setActionId(doc.getObjectId("_id"));
		log.setActionType(Log.INSERT_ACTION);
		log.setActionObject(collName);
		//log.setOperator(Utils.getSessionValue("user"));
		
		this.save(log);
	}
	
	public void updateLog(ObjectId id,Map<String,Object> doc,String collName){
		Log log = new Log();
		log.setActionId(id);
		log.setActionType(Log.UPDATE_ACTION);
		log.setActionObject(collName);
		
		System.out.println("log doc : " + doc);
		log.setContent(doc);
		//log.setOperator(Utils.getSessionValue("user"));
		
		this.save(log);
	}
}
 