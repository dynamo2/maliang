package com.maliang.core.model;

public class UCValue {
	private String value;
	private UCType type;
	private Integer minUnitVal;
	
	public UCValue(){
	}
	
	public UCValue(String v,UCType t){
		this.value = v;
		this.type = t;
	}
	
	public UCValue(int intVal,UCType t){
		this.value = t.toMaxUnit(intVal);
		this.type = t;
	}

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
	
	public int toInt(){
		if(this.minUnitVal == null){
			this.minUnitVal = this.type.toMinUnit(value);
		}
		
		if(this.minUnitVal == null)return 0;
		
		return this.minUnitVal;
	}
	
	public static Object parse(Object val,UCType tp){
		if(val == null)return null;
		
		if(!tp.isValid(val.toString())){
			return val;
		}
		
		return new UCValue(val.toString(),tp);
	}
	
	public String toString(){
		return this.value;
	}
}
