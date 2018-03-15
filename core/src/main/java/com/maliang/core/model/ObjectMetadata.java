package com.maliang.core.model;

import java.util.List;

public class ObjectMetadata extends MongodbModel {
	public final static String TREE_MODEL_PARENT_KEY = "_parent_";
	public final static String TREE_MODEL_PATH_KEY = "_path_";
	
	private String name;
	private String uniqueMark;
	private String label;
	
	@Linked
	private Project project;
	
	//模型类型
	private Integer modelType;
	
	@Mapped(type=ObjectField.class)
	private List<ObjectField> fields;
	
	@Mapped(type=Trigger.class)
	private List<Trigger> triggers;
	
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
	public List<Trigger> getTriggers() {
		return triggers;
	}
	public void setTriggers(List<Trigger> triggers) {
		this.triggers = triggers;
	}
	public Integer getModelType() {
		return modelType;
	}
	public void setModelType(Integer modelType) {
		this.modelType = modelType;
	}
}
