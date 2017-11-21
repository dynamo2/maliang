package com.maliang.core.model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import com.maliang.core.util.Utils;

public class Log extends MongodbModel {
	public static final String NOT_EXIST = "non-existent";
	public static final String ARRAY_PUSH = "_PUSH_";
	public static final String ARRAY_SET = "_SET_";
	
	public static final int INSERT_ACTION = 1;
	public static final int UPDATE_ACTION = 2;
	public static final int DELETE_ACTION = 3;
	
	private ObjectId actionId;
	private int actionType;
	private String actionObject;
	private Map<String,Object> content;
	private Map<String,Object> operator;
	
	private Map<String,Object> oldValue;
	private Map<String,Object> newValue;

	
	public String getActionObject() {
		return actionObject;
	}
	public void setActionObject(String actionObject) {
		this.actionObject = actionObject;
	}
	public ObjectId getActionId() {
		return actionId;
	}
	public void setActionId(ObjectId actionId) {
		this.actionId = actionId;
	}
	public int getActionType() {
		return actionType;
	}
	public void setActionType(int actionType) {
		this.actionType = actionType;
	}
	public Map<String, Object> getContent() {
		return content;
	}
	public void setContent(Map<String, Object> content) {
		this.content = content;
	}
	public Map<String, Object> getOperator() {
		return operator;
	}
	public void setOperator(Map<String, Object> operator) {
		this.operator = operator;
	}
	
	public Map<String,Object> oldValue(){
		if(oldValue == null){
			this.parse();
		}
		return this.oldValue;
	}
	
	public Map<String,Object> newValue(){
		if(newValue == null){
			this.parse();
		}
		
		return this.newValue;
	}
	
	private void parse(){
		this.oldValue = new LinkedHashMap<String,Object>();
		this.newValue = new LinkedHashMap<String,Object>();
		
		if(!Utils.isEmpty(this.content)){
			for(String k : this.content.keySet()){
				Object v = this.content.get(k);
				
				if(Utils.isArray(v) && !Utils.isEmpty(v)){
					Object[] vs = Utils.toArray(v);
					this.oldValue.put(k,vs[0]);
					this.newValue.put(k,vs[1]);
				}
			}
		}
	}
	
	
} 
