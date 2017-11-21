package com.maliang.core.dao;

import com.maliang.core.model.Log;

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
}
 