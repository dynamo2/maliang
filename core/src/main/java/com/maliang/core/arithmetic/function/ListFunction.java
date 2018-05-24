package com.maliang.core.arithmetic.function;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.maliang.core.arithmetic.AE;
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
			if(value instanceof List) {
				return ((List)keyVal).containsAll((List)value);
			}
			return ((List)keyVal).contains(value);
		}
		
		if(keyVal instanceof String) {
			if(Utils.isEmpty(value)) {
				return false;
			}
			
			if(value instanceof List) {
				boolean has = false;
				for(Object o : (List)value) {
					has = ((String) keyVal).indexOf(o.toString()) >= 0;
					if(!has) {
						return false;
					}
				}
				return true;
			}
			
			int idx = ((String) keyVal).indexOf(value.toString());
			return idx >= 0;
		}

		return keyVal != null && value != null && keyVal.equals(value);
	}
	
	public static Object add(Function function ,Map<String,Object> params){
		Object keyVal = function.getKeyValue();
		Object value = function.executeExpression(params);
		
		if(keyVal instanceof List){
			if(value instanceof List){
				((List)keyVal).addAll((List)value);
			}else {
				((List)keyVal).add(value);
			}
		}

		return keyVal;
	}
	
	public static Object prepend(Function function ,Map<String,Object> params){
		Object keyVal = function.getKeyValue();
		Object value = function.executeExpression(params);
		
		if(keyVal instanceof List){
			if(value instanceof List){
				((List)keyVal).addAll(0,(List)value);
			}else {
				((List)keyVal).add(0,value);
			}
		}

		return keyVal;
	}
	
	public static boolean isList(Function function ,Map<String,Object> params){
		Object value = AE.execute(function.getKey(), params);
		if(value == null){
			value = function.executeExpression(params);
		}
		
		return Utils.isArray(value);
	}
	
	public static List<Object> toList(Function function ,Map<String,Object> params){
		Object value = AE.execute(function.getKey(), params);
		if(value == null){
			value = function.executeExpression(params);
		}
		
		if(value == null)return null;
		
		return Utils.toList(value);
	}
	
	public static String join(Function function ,Map<String,Object> params){
		Object keyVal = function.getKeyValue();
		Object value = function.executeExpression(params);
		if(value == null)value = ",";
		
		if(keyVal instanceof List){
			return String.join(value.toString(),(List)keyVal);
		}
		
		if(keyVal == null){
			return "";
		}

		return keyVal.toString();
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
	
	private char nextChar() {
		try {
			return this.source.charAt(cursor);
		}catch(StringIndexOutOfBoundsException e) {
			return '\0';
		}
	}
}
