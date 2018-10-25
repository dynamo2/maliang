package com.maliang.core.model;

import java.util.List;


public class ObjectField extends MongodbModel {
	private String name;
	private String uniqueMark;
	private Integer type;
	private String linkedObject;
	private String label;
	private String relationship;
	private Integer elementType;//鐢ㄤ簬type=array鏃�
	//private Dict dict;//鐢ㄤ簬type=dict鏃�
	
	@Mapped(type=ObjectField.class)
	private List<ObjectField> fields;//鐢ㄤ簬type=inner
	
	/**
	 * 存储类型
	 * 用于第一层的arry[inner]类型:
	 *    1：独立存储: 可以单独操作一条子记录
	 *    2：整存整取：整个数组统一操作
	 * **/
	private Integer storeType = 1;

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
	public List<ObjectField> getFields() {
		return fields;
	}
	public void setFields(List<ObjectField> fields) {
		this.fields = fields;
	}
	
	public boolean isInnerCollection() {
		return FieldType.INNER_COLLECTION.is(this.getType());
	}
	
	public boolean isArray() {
		return FieldType.ARRAY.is(this.getType());
	}
	
	public boolean isLinkCollection() {
		return FieldType.LINK_COLLECTION.is(this.getType());
	}
	
	public boolean isVariableLink() {
		return FieldType.VARIABLE_LINK.is(this.getType());
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
