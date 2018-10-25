package com.model.data;

import java.util.List;

import org.bson.types.ObjectId;

import com.model.db.NotColumn;

public class DictType {
	private String name;
	
	protected ObjectId id;
	
	private String summary;
	
	@NotColumn
	private List<Dict> dicts;

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

	public List<Dict> getDicts() {
		return dicts;
	}

	public void setDicts(List<Dict> dicts) {
		this.dicts = dicts;
	}
}
