package com.model.data;

public class DeviceDict {
	
	//@Column(name="dictType",linked=DictType.class)
	private DictType dictType;
	
	//@Column(name="dict",linked=Dict.class)
	private Dict dict;
	
	public DictType getDictType() {
		return dictType;
	}
	public void setDictType(DictType dictType) {
		this.dictType = dictType;
	}
	public Dict getDict() {
		return dict;
	}
	public void setDict(Dict dict) {
		this.dict = dict;
	}
	
	
}
