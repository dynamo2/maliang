package com.maliang.core.arithmetic.function;

import java.util.Map;

import org.apache.commons.collections.map.LinkedMap;

import com.maliang.core.arithmetic.AE;
import com.maliang.core.arithmetic.ArithmeticExpression;
import com.maliang.core.arithmetic.Substring;
import com.maliang.core.exception.ReturnException;

public class IfFunction {
	private static final String DEFAULT_KEY = "DEFAULT";
	
	private final String source;
	private final int startIndex;
	private int endIndex;
	private Map<String,String> logicCodes = new LinkedMap();
	private int currIndex;
	private String defaultBody = null;
	
	public IfFunction(String s,int sdx){
		this.source = s;
		this.startIndex = sdx;
		currIndex = startIndex;
		
		read();
	}
	
	public static void main(String[] args) {
		String s = "{a:555,i:1}";
		Map<String,Object> params = (Map<String,Object>)ArithmeticExpression.execute(s, null);
		
		s = "{c:if(i=1){{key:1,l:'a'}}elseif(i=2){{key:2,l:'b'}}elseif(a=4){{key:3,l:'c'}}else{[1,2,3,4]},b:333,d:'ggg'}";
		
		Object v = ArithmeticExpression.execute(s, params);
		System.out.println(v);
	}
	
	public Object execute(Map<String,Object> params){
		for(String key:logicCodes.keySet()){
			Object val = ArithmeticExpression.execute(key, params);
			if(val != null && val instanceof Boolean && (Boolean)val){
				
				return AE.execute(logicCodes.get(key), params);
				
			}
		}
		
		return AE.execute(this.defaultBody, params);
		
	}
	
	public int getStartIndex(){
		return this.startIndex;
	}
	
	public int getEndIndex(){
		return this.endIndex;
	}
	
	private void read(){
		currIndex = this.startIndex;
		
		do{
			Substring subs = new Substring(this.source,'(',')',currIndex);
			if(subs.isMatched()){
				String cond = subs.getInnerContent();
				String ifBody = null;
				
				currIndex = subs.getEndIndex();
				subs = new Substring(this.source,')','{',currIndex);
				if(subs.isMatched()){
					String space = subs.getInnerContent();
					
					if(space.trim().isEmpty()){
						subs = new Substring(this.source,'{','}',subs.getEndIndex());
						if(subs.isMatched()){
							ifBody = subs.getInnerContent();
						}
						
						currIndex = subs.getEndIndex();
					}
				}
				
				logicCodes.put(cond, ifBody);
			}else {
				throw new RuntimeException("Error function");
			}
		}while(hasElseif());
		
		readDefault();

		this.endIndex = currIndex;
	}
	
	private boolean hasElseif(){
		Substring subs = new Substring(this.source,'}','(',currIndex);
		boolean is = subs.isMatched() && "elseif".equals(subs.getInnerContent().trim());
		if(is)currIndex = subs.getEndIndex();
		
		return is;
	}
	
	private void readDefault(){
		Substring subs = new Substring(this.source,'}','{',currIndex);
		if(subs.isMatched() && "else".equals(subs.getInnerContent().trim())){
			currIndex = subs.getEndIndex();
			
			subs = new Substring(this.source,'{','}',currIndex);
			if(subs.isMatched()){
				this.defaultBody = subs.getInnerContent();
				
				currIndex = subs.getEndIndex();
			}
		}
	}
}
