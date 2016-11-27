package com.maliang.core.model;

import java.util.List;

public class ObjectMetadata extends MongodbModel {
	private String name;
	private String uniqueMark;
	private String label;
	
	@Linked
	private Project project;
	
	@Mapped(type=ObjectField.class)
	private List<ObjectField> fields;
	
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
	public List<ObjectField> getFields() {
		return fields;
	}
	public void setFields(List<ObjectField> fields) {
		this.fields = fields;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public Project getProject() {
		return project;
	}
	public void setProject(Project project) {
		this.project = project;
	}
}
