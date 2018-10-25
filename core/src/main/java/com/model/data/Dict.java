package com.model.data;

import org.bson.types.ObjectId;

import com.model.db.Column;

public class Dict {
	private String name;
	
	protected ObjectId id;
	
	private String summary;
	
	@Column(name="dictType",linked=DictType.class)
	private DictType dictType;

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

	public DictType getDictType() {
		return dictType;
	}

	public void setDictType(DictType dictType) {
		this.dictType = dictType;
	}
}
