package com.maliang.core.model;

import java.util.List;

@Collection(name="Project")
public class Project extends MongodbModel{
	String name;
	String key;
	
	@Mapped(type=Subproject.class)
	List<Subproject> subprojects;

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

	public List<Subproject> getSubprojects() {
		return subprojects;
	}

	public void setSubprojects(List<Subproject> subprojects) {
		this.subprojects = subprojects;
	}
}
