package com.maliang.core.expression.element;

import com.maliang.core.expression.BasicExpression;
import com.maliang.core.expression.Operator;
import com.mongodb.BasicDBObject;

public abstract class  ElementExpression extends BasicExpression {
	protected String field;
	protected Object fieldValue;
	
	public ElementExpression(String field,Object v,Operator key){
		super(key);
		
		this.field = field;
		this.fieldValue = v;
	}
	
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public Object getFieldValue() {
		return fieldValue;
	}
	public void setFieldValue(Object fieldValue) {
		this.fieldValue = fieldValue;
	}

	public BasicDBObject generateQuery() {
		return new BasicDBObject(field,new BasicDBObject(OPERATOR.getKey(),fieldValue));
	}
}
