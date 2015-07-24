package com.maliang.core.arithmetic.function;

import java.util.Map;
import java.util.Stack;

import com.maliang.core.arithmetic.ArithmeticExpression;
import com.maliang.core.arithmetic.Substring;
import com.maliang.core.service.BusinessService;

public class Function {
	BusinessService businessService = new BusinessService();
	
	//key(expression){body}
	private String key;
	public String expression;
	private String body;
	
	private final String source;
	private final int startIndex;
	private int endIndex;
	
	public Function(String k,String s,int startIndex){
		this.key = k;
		this.source = s;
		this.startIndex = startIndex;
		
		if(key != null)key = key.trim();
		
		readOthers();
	}
	
	public Function(String body){
		this.source = null;
		this.startIndex = -1;
		
		if(body != null){
			this.body = body.trim();
		}
	}
	
	public Object executeExpression(Map<String,Object> params){
		if(this.expression == null || this.expression.trim().isEmpty()){
			return null;
		}
		return ArithmeticExpression.execute(this.expression, params);
	}
	
	public static void pushThis(Map<String,Object> params){
		if(params == null || !params.containsKey("this"))return;
		
		if(!params.containsKey("these")){
			params.put("these", new Stack());
		}
		((Stack)params.get("these")).push(params.get("this"));
	}
	
	public static void popThis(Map<String,Object> params){
		if(params == null || !params.containsKey("these"))return;
		
		Stack these = (Stack)params.get("these");
		params.put("this", these.pop());
		if(these.size() == 0){
			params.remove("these");
		}
	}
	
	boolean isMap(){
		return key == null && this.body.startsWith("{") && this.body.endsWith("}");
	}
	
	boolean isList(){
		return key == null && this.body.startsWith("[") && this.body.endsWith("]");
	}
	
	public Object execute(Map<String,Object> params){
		if(this.isMap()){
			return MapFunction.execute(this, params);
		}
		
		if("sum".equals(key)){
			return Sum.execute(this, params);
		}
		
		if("each".equals(key)){
			return Each.execute(this, params);
		}
		
		if("if".equals(key)){
			return If.execute(this, params);
		}
		
		if("addToParams".equals(key)){
			return AddToParams.execute(this, params);
		}
		
		if("business".equals(key)){
			return business(params);
		}
		
		if(key != null && key.startsWith("db.")){
			return DBFunction.execute(this, params);
		}
		
		return ListFunction.execute(this, params);
	}
	
	private Object business(Map<String,Object> params){
		Object value = this.executeExpression(params);
		if(value != null && value instanceof Map){
			Map<String,Object> map = (Map<String,Object>)value;

			return this.businessService.business(map);
		}
		return null;
	}
	
	public void setBody(String bd){
		this.body = bd;
	}
	
	public int getEndIndex(){
		return this.endIndex;
	}
	
	public String getKey(){
		return this.key;
	}
	
	public String getExpression(){
		return this.expression;
	}
	
	public String getBody(){
		return this.body;
	}
	
	public boolean isEmptyBody(){
		if(body == null || this.body.trim().isEmpty()){
			return true;
		}
		
		return false;
	}
	
	private void readOthers(){
		int i = this.startIndex;
		
		Substring subs = new Substring(this.source,'(',')',i);
		if(subs.isMatched()){
			this.expression = subs.getInnerContent();
			
			i = subs.getEndIndex();
			subs = new Substring(this.source,')','{',i);
			if(subs.isMatched()){
				String space = subs.getInnerContent();
				
				i = subs.getEndIndex();
				if(space.trim().isEmpty()){
					subs = new Substring(this.source,'{','}',i);
					
					i = subs.getEndIndex();
					if(subs.isMatched()){
						this.body = subs.getInnerContent();
					}
				}
			}
		}else {
			throw new RuntimeException("Error function");
		}
		
		this.endIndex = i;
	}
	
	public String toString(){
		return key + "("+this.expression+")" +( body == null?"":"{"+body+"}");
	}
}
