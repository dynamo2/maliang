package com.maliang.core.model;

import java.util.Map;

public class Dict {
	private ObjectField key;
	private ObjectField value;
	private Map<Object,Object> dicts;
	
	public ObjectField getKey() {
		return key;
	}
	public void setKey(ObjectField key) {
		this.key = key;
	}
	public ObjectField getValue() {
		return value;
	}
	public void setValue(ObjectField value) {
		this.value = value;
	}
	public Map<Object, Object> getDicts() {
		return dicts;
	}
	public void setDicts(Map<Object, Object> dicts) {
		this.dicts = dicts;
	}
	
	
}
