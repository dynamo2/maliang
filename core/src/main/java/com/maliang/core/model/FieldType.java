package com.maliang.core.model;


public enum FieldType {
	STRING(1,"string"),DOUBLE(2,"double"),INT(3,"int"),INNER(4,"inner"),RELATED(5,"related"),
	FILE(6,"file"),PICTURE(7,"picture");
	
	private int code = -1;
	private String name;
	
	private FieldType(int c,String k){
		this.code = c;
		this.name = k;
	}
	
	public String getName(){
		return this.name;
	}
	
	public int getCode(){
		return this.code;
	}
	
	public static String getName(int code){
		for(FieldType type:FieldType.values()){
			if(type.code == code){
				return type.name;
			}
		}
		
		return null;
	}
}
