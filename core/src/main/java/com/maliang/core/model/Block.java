package com.maliang.core.model;

public class Block extends MongodbModel {
	public static final int TYPE_CODE = 1;
	public static final int TYPE_HTML = 2;
	
	private String name;
	private String code;
	
	/**
	 * 1:code
	 * 2:html
	 * ***/
	private Integer type;
	
	

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
}
