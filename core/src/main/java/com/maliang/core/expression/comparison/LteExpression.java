package com.maliang.core.expression.comparison;

import java.util.Map;

import com.maliang.core.expression.Operator;



public class LteExpression extends ComparisonExpression {
	public LteExpression(String f,Object fv){
		super(f,fv,Operator.LTE);
	}
	public LteExpression(String source,Map<String,Object> paramMap){
		super(source,paramMap,Operator.LTE);
		
	}
}
