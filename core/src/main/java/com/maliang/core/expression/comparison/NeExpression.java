package com.maliang.core.expression.comparison;

import java.util.Map;

import com.maliang.core.expression.Operator;



public class NeExpression extends ComparisonExpression {
	public NeExpression(String f,Object fv){
		super(f,fv,Operator.NE);
	}
	public NeExpression(String source,Map<String,Object> paramMap){
		super(source,paramMap,Operator.NE);
	}
}
