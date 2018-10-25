package com.model.data;

import org.bson.types.ObjectId;

import com.model.dao.DBModel;
import com.model.db.Column;
import com.model.db.Table;

@Table(name="EZ_ModelType")
public class ModelType extends DBModel{
	private String name;
	
	protected ObjectId id;
	
//	@Column(name="_parent",linked=ModelType.class)
	private ModelType parent;

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

	public ModelType getParent() {
		return parent;
	}

	public void setParent(ModelType parent) {
		this.parent = parent;
	}
}
