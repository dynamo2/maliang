package com.maliang.core.arithmetic.function;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.maliang.core.arithmetic.ArithmeticExpression;
import com.maliang.core.arithmetic.node.Parentheses;
import com.maliang.core.util.Utils;

public class ListFunction {
	public static Object execute(Function function ,Map<String,Object> params){
		if(function.isList()){
			ListCompiler compiler = new ListCompiler(function.getBody(),1,params);
			return compiler.getList();
		}
		
		Object expressionData = function.executeExpression(params);
		Object keyData = ArithmeticExpression.execute(function.getKey(), params);
		if(keyData == null)return null;
		
		if(!(expressionData instanceof Integer)){
			throw new RuntimeException("Error array index");
		}
		
		int index = (Integer)expressionData;
		if(keyData instanceof Collection){
			keyData = ((Collection)keyData).toArray();
		}
		
		if(!(keyData instanceof Object[])){
			throw new RuntimeException("Error type");
		}
		
		if(index < 0 && index >= ((Object[])keyData).length){
			throw new RuntimeException(index+" is out of array");
		}
		
		return ((Object[])keyData)[index];
	}
	
	public static boolean contains(Function function ,Map<String,Object> params){
		Object keyVal = function.getKeyValue();
		Object value = function.executeExpression(params);
		
		if(keyVal instanceof List){
			return ((List)keyVal).contains(value);
		}

		return keyVal != null && value != null && keyVal.equals(value);
	}
	
	public static boolean isList(Function function ,Map<String,Object> params){
		Object value = ArithmeticExpression.execute(function.getKey(), params);
		if(value == null){
			value = function.executeExpression(params);
		}
		
		return Utils.isArray(value);
	}
	
	public static List<Object> toList(Function function ,Map<String,Object> params){
		Object value = ArithmeticExpression.execute(function.getKey(), params);
		if(value == null){
			value = function.executeExpression(params);
		}
		
		if(value == null)return null;
		if(value instanceof List)return (List)value;
		if(value instanceof Object[])return Arrays.asList((Object[])value);
		
		List<Object> l = new ArrayList<Object>();
		l.add(value);
		
		return l;
	}
}

class ListCompiler {
	private int cursor = 0;
	private String source = null;
	private List<Object> list = null;
	private Map<String,Object> params;
	private char[] endChars = new char[]{',',']'};
	
	ListCompiler(String source,int s,Map<String,Object> params){
		this.cursor = s;
		this.source = source;
		this.params = params;
		
		this.list = readToList();
	}
	
	public List<Object> getList(){
		return this.list;
	}
	
	private List<Object> readToList(){
		List<Object> list = new ArrayList<Object>();
		char c = 0;
		for(; cursor < this.source.length();){
			c = readChar();
			
			if(c == ']'){
				return list;
			}
			
			if(c == ','){
				continue;
			}
			
			Parentheses pt = Parentheses.compile(source, this.cursor-1, endChars);
			list.add(pt.getValue(this.params));
			
			this.cursor = pt.getEndIndex()+1;
		}
		
		return list;
	}
	
	private char readChar(){
		return this.source.charAt(cursor++);
	}
}
