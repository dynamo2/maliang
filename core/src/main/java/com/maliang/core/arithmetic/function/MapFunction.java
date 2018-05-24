package com.maliang.core.arithmetic.function;

import java.util.HashMap;
import java.util.Map;

import com.maliang.core.arithmetic.AE;
import com.maliang.core.arithmetic.ArithmeticExpression;
import com.maliang.core.arithmetic.Reader;
import com.maliang.core.arithmetic.node.Parentheses;
import com.maliang.core.service.MapHelper;
import com.maliang.core.util.Utils;

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
		
		
//		Object formMap = ArithmeticExpression.execute(form, params);
//		System.out.println(formMap);
		
		
		
		String s = "addToParams({pps:[{name:'33',total:null},ddd:fff,{name:'22',total:9},,{name:'11',total:9},{name:'88',total:0}],b:pps.findOne(name='22'),"
				+ "isDisable(n,t,fname)::{[\r\n" + 
				"        item.set(pps.findOne(n=name)),\r\n" + 
				"        if(item.total.isNull() | item.total=0){\r\n" + 
				"            s.set('disabled')\r\n" + 
				"        }elseif(item.total<t){\r\n" + 
				"            s.set('total < t')\r\n"+ 
				"        }else {\r\n" + 
				"            s.set('show')\r\n" + 
				"        },\r\n" + 
				"        return(s+'-'+t+'=='+fname)\r\n" + 
				"    ]},i33:isDisable('33',3,'aaa'),i88:isDisable('88',8,'999'),i11:isDisable('11',88),i22:isDisable('22',2)})";
		
		
		s = "addToParams({pps:[{name:'33',total:null},ddd(name,price)::{name+'--fff--'+price},{name:'22',total:9},,{name:'11',total:9},{name:'88',total:0}],b:pps.findOne(name='22'),"
				+ "isDisable(n,t,fname)::{[\r\n" + 
				"        item.set(pps.findOne(n=name)),\r\n" + 
				"        if(item.total.isNull() | item.total=0){\r\n" + 
				"            s.set('disabled')\r\n" + 
				"        }elseif(item.total<t){\r\n" + 
				"            s.set('total < t')\r\n"+ 
				"        }else {\r\n" + 
				"            s.set('show')\r\n" + 
				"        },\r\n" + 
				"        return(s+'-'+t+'=='+fname)\r\n" + 
				"    ]},i33:isDisable('33',3,'aaa'),i88:isDisable('88',8,'999'),i11:isDisable('11',88),i22:isDisable('22',2),d111:ddd('ddd',99)})";
		
	
		
//		s = "{pps:[{name:'33',total:null},{name:'22',total:9},{name:'88',total:0}],b:pps.findOne(name='22'),"
//				+ "isDisable(n)::{[\r\n" +  
//				"        if(true){\r\n" + 
//				"            s.set('disabled')\r\n" + 
//				"        }else {\r\n" + 
//				"            s.set('')\r\n" + 
//				"        }\r\n,return(s)" + 
//				"    ]},call:isDisable('33')}";
		
		Map<String,Object> ps = new HashMap<String,Object>();
		Object obj = AE.execute(s,ps);
		//System.out.println(ps);
		System.out.println("-------------------------");
		System.out.println("obj : "+obj);
		
		
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
	private boolean isExecute = true; //是否计算
	
	MapCompiler(String source,int s,Map<String,Object> params){
		this.cursor = s;
		this.source = source;
		this.params = params;
		//this.addToParams = true;
		
		this.map = readToMap();
	}
	
	MapCompiler(String source,int s,Map<String,Object> params,boolean isAddToParams,boolean isExe){
		this.cursor = s;
		this.source = source;
		this.params = params;
		this.addToParams = isAddToParams;
		this.isExecute = isExe;
		
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
		Map<String,Object> map = new HashMap<String,Object>();
		char c = 0;
		this.clearCache();
		for(; cursor < this.source.length();){
			c = readChar();
			
			/**
			 * 解析注释代码
			 * **/
			if(c == '/' && this.nextChar() == '*'){
				Reader sbs = new Reader(source,"/*","*/",this.cursor-1,false);
				this.cursor = sbs.getEndIndex()+"*/".length();
				
				continue;
			}
			
			if(c == '}'){
				return map;
			}
			
			if(c == '('){
				Reader r = new Reader(this.source,"(",")",this.cursor-1,true);
				
				this.sbf.append(r.getCompleteContent());
				this.cursor = r.getEndIndex();
				
				continue;
			}
			
			if(c == ':'){
				key = read(sbf);
				
				if(this.nextChar() == ':') {
					this.cursor = UserFunction.readUserFunction(params,this.source,key,this.cursor);
					clearCache();
				}else {
					clearBuffer();
				}
				
				continue;
			}
			
			if(c == ','){
				this.clearCache();
				continue;
			}
			
			if(key != null){

				Parentheses pt = Parentheses.compile(source, this.cursor-1, endChars);
				Object value = null;
				if(this.isExecute){
					value = pt.getValue(this.params);
				}else {
					value = pt.expressionStr();
				}
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
	private char nextChar() {
		try {
			return this.source.charAt(cursor);
		}catch(StringIndexOutOfBoundsException e) {
			return '\0';
		}
	}
}
