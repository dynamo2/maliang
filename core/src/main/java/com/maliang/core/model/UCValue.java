package com.maliang.core.model;

public class UCValue {
	private String value;
	private UCType type;

	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public UCType getType() {
		return type;
	}
	public void setType(UCType type) {
		this.type = type;
	}
	
	public String toString(){
		return this.value;
	}
}
