package com.maliang.core.expression.element;

import com.maliang.core.expression.Operator;

public class TypeExpression extends ElementExpression {
	public TypeExpression(String field,Integer type){
		super(field,type,Operator.TYPE);
	}
}
