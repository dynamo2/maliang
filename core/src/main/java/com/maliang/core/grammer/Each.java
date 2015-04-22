package com.maliang.core.grammer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.maliang.core.arithmetic.ArithmeticExpression;
import com.maliang.core.service.DBData;
import com.maliang.core.service.MapHelper;

public class Each {
	private final String sourceCode;
	private final Map<String,Object> parameters;
	private List<Object> dataList;
	private List<Object> resultList;
	private String eachExpression;
	private int currIndex = 0;
	private EachNode eachNode;
	
	public Each(String code,Map<String,Object> params){
		this.sourceCode = code;
		this.parameters = params;
		
		compile();
	}
	
	public static void main(String[] args) {
		String str = "each(  products  ){product:this,"
				+ "price:user.user_grade.discount*0.01*this.price}";
		
		List<Map<String,Object>> products = DBData.list("Product");
		Map<String,Object> params = new HashMap<String,Object>();
		Map<String,Object> user = DBData.getRandom("User");
		
		params.put("products", products);
		params.put("user", user);
		
		Each e = new Each(str,params);
		
		System.out.println("eachExpression : '" + e.eachExpression +"'");
		System.out.println("eachNode: " + e.eachNode);
		
		System.out.println("each : " + e.execute());
		
		
	}
	
	private void compile(){
		this.reset();
		
		readEachExpression();
		readEachNode();
		
		dataList = (List<Object>)MapHelper.readValue(this.parameters,this.eachExpression);
	}
	
	public List<Object> execute(){
		if(resultList != null){
			return this.resultList;
		}
		
		resultList = new ArrayList<Object>(dataList.size());
		if(dataList == null || dataList.size() == 0){
			return resultList;
		}
		
		for(Object data : dataList){
			this.parameters.put("this", data);
			
			resultList.add(this.eachNode.execute(this.parameters));
		}
		
		this.parameters.remove("this");
		return resultList;
	}
	
	private void reset(){
		currIndex = 0;
		dataList = null;
		this.eachNode = null;
		this.eachExpression = null;
		resultList = null;
	}
	
	private void readEachNode(){
		if(this.hasNext()){
			int[] coors = MapHelper.matchCoordinate(this.sourceCode, '{', '}', currIndex);
			
			if(coors[0] >= 0 && coors[1] >= 0){
				String str = this.sourceCode.substring(coors[0],coors[1]+1);
				Map<String,Object> map = MapHelper.curlyToMap(str);
				if(map.isEmpty()){
					this.eachNode = new ExpressionNode(str);
				}else {
					this.eachNode = new MapNode(map);
				}
				return;
			}
		}
		
		eachNode = new EmptyNode();
	}
	
	private void readEachExpression(){
		int left = this.sourceCode.indexOf('(');
		String key = this.sourceCode.substring(0,left).trim();
		if(!"each".equals(key)){
			throw new RuntimeException("Each compile error");
		}
		
		currIndex = left+1;
		StringBuffer sbf = new StringBuffer();
		while(hasNext()){
			char c = next();
			if(c == ')'){
				break;
			}
			
			sbf.append(c);
		}
		this.eachExpression = sbf.toString().trim();
	}
	
	private char next(){
		if(currIndex >= this.sourceCode.length()){
			throw new RuntimeException("Over length");
		}
		return this.sourceCode.charAt(currIndex++);
	}
	
	private boolean hasNext(){
		return currIndex >= 0 && currIndex < this.sourceCode.length();
	}
	
	static class EachNode {
		Object execute(Map<String,Object> params){
			return null;
		}
	}
	
	static class EmptyNode extends EachNode {
		
	}
	
	static class MapNode extends EachNode {
		Map<String,Object> eachMap;
		MapNode(Map<String,Object> map){
			this.eachMap = map;
		}
		
		Object execute(Map<String,Object> params){
			Map<String,Object> newObj = new HashMap<String,Object>();
			
			for(Map.Entry<String, Object> entry : this.eachMap.entrySet()){
				String expre = (String)entry.getValue();
				Object value = ArithmeticExpression.execute(expre, params);

				newObj.put(entry.getKey(), value);
			}
			
			return newObj;
		}
		
		public String toString(){
			return this.eachMap.toString();
		}
	}
	
	static class ExpressionNode extends EachNode {
		String expression;
		
		ExpressionNode(String expre){
			this.expression = expre;
		}
		
		Object execute(Map<String,Object> params){
			return ArithmeticExpression.execute(this.expression, params);
		}
		
		public String toString(){
			return this.expression;
		}
	}
}
