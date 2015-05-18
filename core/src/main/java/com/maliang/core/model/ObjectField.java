package com.maliang.core.model;


public class ObjectField extends MongodbModel {
	private String name;
	private String uniqueMark;
	private int type;
	private String linkedObject;
	private String label;
	private String relationship;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUniqueMark() {
		return uniqueMark;
	}
	public void setUniqueMark(String uniqueMark) {
		this.uniqueMark = uniqueMark;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getLinkedObject() {
		return linkedObject;
	}
	public void setLinkedObject(String linkedObject) {
		this.linkedObject = linkedObject;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getRelationship() {
		return relationship;
	}
	public void setRelationship(String relationship) {
		this.relationship = relationship;
	}
}
