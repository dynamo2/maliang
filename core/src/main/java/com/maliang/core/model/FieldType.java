package com.maliang.core.model;


public enum FieldType {
	INT(1,"int"),DOUBLE(2,"double"),STRING(3,"string"),DATE(4,"date"),
	FILE(5,"file"),//文件
	DICT(6,"dict"), //数据字典
	INNER_COLLECTION(7,"inner"),//内嵌集合
	LINK_COLLECTION(8,"link"),
	ARRAY(9,"array");//关联集合
	
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
	
	public boolean is(int c){
		return this.code == c;
	}
	
	public boolean is(String s){
		return this.name.equals(s);
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
