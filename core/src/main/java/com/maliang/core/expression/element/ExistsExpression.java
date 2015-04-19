package com.maliang.core.expression.element;

import com.maliang.core.expression.Operator;

public class ExistsExpression extends ElementExpression {
	public ExistsExpression(String field,boolean exits){
		super(field,exits,Operator.EXISTS);
	}
}
