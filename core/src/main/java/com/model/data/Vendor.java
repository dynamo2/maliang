package com.model.data;

import org.bson.types.ObjectId;

import com.model.db.Table;

@Table(name="EZ_Vendor")
public class Vendor {
	private String name;
	
	protected ObjectId id;
	
	private String summary;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}
}
