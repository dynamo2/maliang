package com.model.dao;

import java.util.Date;

import org.bson.types.ObjectId;

public class DBModel {
	protected ObjectId id;
	protected Date createdDate;
	protected Date modifiedDate;
	
	public ObjectId getId() {
		return id;
	}
	
	public void setId(ObjectId id) {
		this.id = id;
	}
	
	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
}
