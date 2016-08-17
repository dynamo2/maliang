package com.maliang.core.dao;

import com.maliang.core.model.UCType;

public class UCTypeDao  extends ModelDao<UCType> {
	protected static String COLLECTION_NAME = "UCType";
	public UCTypeDao(){
		super(COLLECTION_NAME,UCType.class);
	}

	public UCType getByKey(int key){
		return this.getByField("key",key);
	}
	
	public UCType getByName(String name){
		return this.getByField("name",name);
	}
	public static void main(String[] args) {
		UCTypeDao dao = new UCTypeDao();
	}
}
