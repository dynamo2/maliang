package com.maliang.core.model;


public class ObjectField extends MongodbModel {
	private String name;
	private String uniqueMark;
	private Integer type;
	private String linkedObject;
	private String label;
	private String relationship;
	private Integer elementType;//用于type=array时

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
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
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
	public Integer getElementType() {
		return elementType;
	}
	public void setElementType(Integer elementType) {
		this.elementType = elementType;
	}
}

/*
class Type {
	int code;
	String linkedObject;
	Type next;
	Type pre;
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getLinkedObject() {
		return linkedObject;
	}
	public void setLinkedObject(String linkedObject) {
		this.linkedObject = linkedObject;
	}
	public Type getNext() {
		return next;
	}
	public void setNext(Type next) {
		this.next = next;
	}
	public Type getPre() {
		return pre;
	}
	public void setPre(Type pre) {
		this.pre = pre;
	}
}*/
