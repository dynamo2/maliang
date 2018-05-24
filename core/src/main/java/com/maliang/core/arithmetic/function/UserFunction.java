package com.maliang.core.arithmetic.function;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.maliang.core.arithmetic.AE;
import com.maliang.core.arithmetic.Reader;
import com.maliang.core.service.MapHelper;
import com.maliang.core.util.Utils;

/***
 * 用户自定义函数
 * **/
public class UserFunction {
	public static final String PARAMS_KEY = "__USER_FUNCTION__";
	
	private String key;
	private String paramenter;
	private String body;
	private String[] pnames;
	
	public UserFunction() {
	}
	
	public UserFunction(String k,String p,String b) {
		this.setKey(k);
		this.setParamenter(p);
		this.setBody(b);
	}
	
	public static int readUserFunction(Map<String,Object> params,String source,String key,int index) {
		UserFunction uFun = new UserFunction();
		uFun.readKeyAndParamenter(key);
		
		String fbody = null;
		if(UserFunction.hasBody(source,index+1)) {
			Reader br = new Reader(source,"{","}",index,true);
			fbody = br.getInnerContent();
			
			index = br.getEndIndex()+1;
		}else {
			index++;
		}
		
		uFun.setBody(fbody);
		UserFunction.put(params, uFun);
		
//		System.out.println("**************************************");
//		System.out.println(" key : " + key);
//		System.out.println(" source : " + source);
//		System.out.println(uFun);
//		System.out.println("**************************************");
		
		return index;
	}
	
	public void readKeyAndParamenter(String str) {
		Reader kr = new Reader(str,"(",")",0,true);
		String fp = kr.getInnerContent();
		
		String fk = str;
		if(kr.isMatched()) {
			fk = str.substring(0, kr.getStartIndex());
		}
		
		this.setKey(fk);
		this.setParamenter(fp);
	}
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public String getParamenter() {
		return paramenter;
	}

	public void setParamenter(String paramenter) {
		this.paramenter = paramenter;
		if(!Utils.isEmpty(this.paramenter)) {
			pnames = this.paramenter.split(",");
		}
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	
	
	public Object execute(Function fun,Map<String,Object> params) {
		String expre = fun.expression;
		if(!Utils.isEmpty(expre) && !isMap(expre) && !isList(expre)) {
			expre = "["+expre+"]";
		}
		
		Object val = AE.execute(expre,params);
		Map<String,Object> userParams = new HashMap<String,Object>();
		userParams.putAll(params);
		
		if(!Utils.isEmpty(pnames)) {
			for(int i = 0; i < pnames.length; i++) {
				String name = pnames[i];
				Object pv = getParamVal(Utils.toList(val),i);
				
				userParams.put(name, pv);
			}
		}
		
		val = AE.execute(this.body, userParams);
		
		System.out.println("--- val : " + val);
		return val;
	}
	
	private Object getParamVal(List vals,int i) {
		if(Utils.isEmpty(vals)) {
			return null;
		}
		
		if(vals.size() <= i) {
			return null;
		}
		return vals.get(i);
	}
	
	boolean isMap(String s){
		return !Utils.isEmpty(s) && s.startsWith("{") && s.endsWith("}");
	}
	
	boolean isList(String s){
		return !Utils.isEmpty(s) && s.startsWith("[") && s.endsWith("]");
	}
	
	public static boolean hasBody(String source,int index) {
		if(Utils.isEmpty(source) || index < 0) {
			return false;
		}
		for(int i = index; i < source.length(); i++) {
			char c = source.charAt(i);
			if(c == ' ' || c == '	') {
				continue;
			}
			if(c == '{') {
				return true;
			}
			return false;
		}
		return false;
	}
	
	public static void put(Map<String,Object> params,UserFunction uFun) {
		if(uFun == null) {
			return;
		}
		
		Object funs = MapHelper.readValue(params,UserFunction.PARAMS_KEY);
		if(!(funs instanceof Map) || Utils.isEmpty(funs)) {
			funs = new HashMap<String,Object>();
			params.put(UserFunction.PARAMS_KEY, funs);
		}
		
		((Map)funs).put(uFun.getKey(),uFun);
	}
	
	public static UserFunction readFunction(Map<String,Object> params,String key) {
		Object val = MapHelper.readValue(params,PARAMS_KEY+"."+key);
		if(val == null || !(val instanceof UserFunction)) {
			return null;
		}
		
		return (UserFunction)val;
	}
	
	public String toString() {
		return "\n#######\nkey:"+key+"\nparameter:"+this.paramenter+"\nbody:"+this.body+"\n#######\n";
	}
}
