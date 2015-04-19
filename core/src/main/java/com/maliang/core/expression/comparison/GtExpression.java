package com.maliang.core.expression.comparison;

import java.util.Map;

import com.maliang.core.expression.Operator;

public class GtExpression extends ComparisonExpression {
	public GtExpression(String f,Object fv){
		super(f,fv,Operator.GT);
	}
	public GtExpression(String source,Map<String,Object> paramMap){
		super(source,paramMap,Operator.GT);
		
	}
}
