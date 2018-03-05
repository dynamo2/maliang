package com.maliang.core.model;


public enum ModelType {
	DEFAULT(1,"default"),//��ͨdocument��Ĭ��
	TREE(2,"tree");//tree�ṹ
	
	private int code = -1;
	private String name;
	
	private ModelType(int c,String k){
		this.code = c;
		this.name = k;
	}
	
	public String getName(){
		return this.name;
	}
	
	public int getCode(){
		return this.code;
	}
	
	public boolean is(Integer c){
		if(c == null)return false;
		return this.code == c;
	}
	
	public boolean is(String s){
		return this.name.equals(s);
	}
	
	public static String getName(int code){
		for(ModelType type:ModelType.values()){
			if(type.code == code){
				return type.name;
			}
		}
		
		return null;
	}
}
