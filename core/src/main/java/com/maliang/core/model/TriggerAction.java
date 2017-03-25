package com.maliang.core.model;

public class TriggerAction extends MongodbModel{
	/**
	 * 
	 * **/
	private String field;
	
	/**
	 * 触发器执行的代码
	 * **/
	private String code;

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
