package com.maliang.core.arithmetic.function;

import java.util.HashMap;
import java.util.Map;

import com.maliang.core.arithmetic.ArithmeticExpression;
import com.maliang.core.arithmetic.Substring;
import com.maliang.core.service.MapHelper;

public class MapFunction {
	public static Object execute(Function function ,Map<String,Object> params){
		MapCompiler compiler = new MapCompiler(function.getBody(),1,params);
		
		return compiler.getMap();
	}
	
	public static void main(String[] args) {
		
		String paramStr = "{product:{id:'111111',name:'Product',brand:'aaaa',price:335.8,expiry_date:'2015-03-26日23时33分23秒'},"
				+ "brands:[{id:'aaaa',name:'雪花秀'},{id:'bbbb',name:'希思黎'},{id:'cccc',name:'Pola'}]}";
		Map<String,Object> params = MapHelper.buildAndExecuteMap(paramStr, null);
		//System.out.println(params);
		
		String form = "{type:'form',action:'',name:'product.edit.form',"
				+ "inputs:[{name:'product.id',type:'hidden',value:product.id},"
					+ "{name:'product.name',type:'text',value:product.name},"
					+ "{name:'product.brand',type:'select',value:product.brand,"
						+ "options:each(brands){key:this.id,value:this.name}},"
					+ "{name:'product.price',type:'double',value:product.price},"
					+ "{name:'product.expiry_date',type:'date',value:product.expiry_date},"
					+ "{name:'product.picture',type:'file',value:product.picture}]}";
		
		/*
		form = "{name:'product.brand',type:'select',value:product.brand,"
						+ "input:{name:'product.id',type:'hidden',value:product.id},"
						+ "options:each(brands){key:this.id,value:this.name},"
						+ "action:'edit.html',price:{name:'product.price',type:'double',value:product.price}}";
						*/
		
		//form = "{expiry_date:D'20150326 23:33:23'}";
		Object formMap = ArithmeticExpression.execute(form, params);
		System.out.println(formMap);
		
		//Substring sbs = new Substring("{name:'2009-3-9'}",'\'',0);
		//System.out.println(sbs.getCompleteContent());
	}
}

class MapCompiler {
	private int cursor = 0;
	private String source = null;
	private Map<String,Object> map = null;
	private String key = null;
	private StringBuffer sbf = null;
	private Map<String,Object> params;
	private char[] endChars = new char[]{',','}'};
	private boolean addToParams = false;
	
	MapCompiler(String source,int s,Map<String,Object> params){
		this.cursor = s;
		this.source = source;
		this.params = params;
		this.addToParams = true;
		
		this.map = readToMap();
	}
	
	MapCompiler(String source,int s,Map<String,Object> params,boolean isAddToParams){
		this.cursor = s;
		this.source = source;
		this.params = params;
		this.addToParams = isAddToParams;
		
		this.map = readToMap();
	}
	
	public Map<String,Object> getMap(){
		return this.map;
	}
	
	private String read(StringBuffer sbf){
		if(sbf == null){
			return null;
		}
		
		return sbf.toString().trim();
	}
	
	private Map<String,Object> readToMap(){
		//System.out.println("addToParams : " + this.addToParams);
		
		Map<String,Object> map = new HashMap<String,Object>();
		char c = 0;
		this.clearCache();
		for(; cursor < this.source.length();){
			c = readChar();
			
			if(c == '}'){
				return map;
			}
			if(c == ':'){
				key = read(sbf);
				clearBuffer();
				continue;
			}
			
			if(c == ','){
				this.clearCache();
				continue;
			}
			
			if(key != null){
				ArithmeticExpression.Parentheses pt = ArithmeticExpression.Parentheses.compile(source, this.cursor-1, endChars);
				Object value = pt.getValue(this.params);
				map.put(key, value);
				
				if(addToParams && params != null){
					params.put(key, value);
				}
				
				this.cursor = pt.getEndIndex()+1;
				this.clearCache();
				continue;
			}
			if(sbf == null){
				sbf = new StringBuffer("");
			}
			sbf.append(c);
		}

		return map;
	}
	
	private void clearBuffer(){
		sbf = null;
	}
	
	private void clearCache(){
		sbf = null;
		key = null;
	}
	private char readChar(){
		return this.source.charAt(cursor++);
	}
}
