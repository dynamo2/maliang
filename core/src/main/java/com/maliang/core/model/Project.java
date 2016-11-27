package com.maliang.core.model;

@Collection(name="System")
public class Project extends MongodbModel{
	String name;
	String key;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
}
