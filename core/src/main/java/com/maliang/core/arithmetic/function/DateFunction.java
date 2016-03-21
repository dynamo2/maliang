package com.maliang.core.arithmetic.function;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.maliang.core.arithmetic.ArithmeticExpression;
import com.maliang.core.service.CollectionService;

public class DateFunction {
	public static String dateFormat(Function function,Map<String,Object> params){
		Map<String,String> formatNames = new HashMap<String,String>();
		formatNames.put("/", "yyyy/MM/dd");
		formatNames.put("-","yyyy-MM-dd");
		formatNames.put("cn","yyyy年MM月dd日");
		
		return doFormat(function,params,CollectionService.dateFormat,formatNames);
	}
	
	public static String timestampFormat(Function function,Map<String,Object> params){
		Map<String,String> formatNames = new HashMap<String,String>();
		formatNames.put("/", "yyyy/MM/dd HH:mm:ss");
		formatNames.put("-","yyyy-MM-dd HH:mm:ss");
		formatNames.put("cn","yyyy年MM月dd日 HH:mm:ss");
		
		return doFormat(function,params,CollectionService.timestampFormat,formatNames);
	}
	
	private static String doFormat(Function function,Map<String,Object> params,DateFormat defaultFormat,Map<String,String> formatNames){
		DateFormat format = defaultFormat;
		Object val = function.getKeyValue();
		Object fp = function.executeExpression(params);
		if(val == null){
			val = fp;
		}else {
			if(fp != null && fp instanceof String){
				try {
					String fs = formatNames.get((String)fp);
					if("/".equals(fs)){
						fs = "yyyy/MM/dd HH:mm:ss";
					}else if("-".equals(fs)){
						fs = "yyyy-MM-dd HH:mm:ss";
					}else if("cn".equals(fs)){
						fs = "yyyy年MM月dd日 HH:mm:ss";
					}
					
					if(fs != null){
						format = new SimpleDateFormat(fs);
					}
				}catch(Exception e){
					format = defaultFormat;
				}
			}
		}
		
		if(val == null || !(val instanceof Date)){
			return "";
		}
		
		try {
			return format.format((Date)val);
		}catch(Exception e){
			return "";
		}
	}
	
	public static void main(String[] args) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("d",new Date());
		
		String s = "D'2003年11月30日 12时21分22秒'.df('/')";
		Object u = ArithmeticExpression.execute(s,params);
		
		System.out.println("u : " + u);
	}
	private static Date getDateValue(Function function,Map<String,Object> params){
		Object val = function.getKeyValue();
		if(val == null){
			val = function.executeExpression(params);
		}
		
		if(val == null || !(val instanceof Date)){
			return null;
		}
		return (Date)val;
	}
}
