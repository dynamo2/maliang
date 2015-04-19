package com.maliang.core.expression.comparison;

import java.util.Map;

import com.maliang.core.expression.Operator;



public class EqExpression extends ComparisonExpression {
	public EqExpression(String f,Object fv){
		super(f,fv,Operator.EQ);
	}
	public EqExpression(String source,Map<String,Object> paramMap){
		super(source,paramMap,Operator.EQ);
		
	}
}
