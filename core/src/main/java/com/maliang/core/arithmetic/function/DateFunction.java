package com.maliang.core.arithmetic.function;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.maliang.core.arithmetic.AE;
import com.maliang.core.arithmetic.ArithmeticExpression;
import com.maliang.core.service.CollectionService;
import com.maliang.core.util.Utils;

public class DateFunction {
	public static Object date(Function function,Map<String,Object> params){
		Object val = function.getKeyValue();
		if(val == null){
			val = function.executeExpression(params);
		}
		
		if(val != null){
			return Utils.parseDate(val.toString());
		}
		
		return new Date();
	}
	
	public static Object hourFormat(Function function,Map<String,Object> params){
		Object val = function.getKeyValue();
		if(val == null){
			val = function.executeExpression(params);
		}
		
		Long time = null;
		try {
			time = Long.valueOf(val.toString());
			if(time < 0)return null;
			
			StringBuffer buf = new StringBuffer();
			
			long h = time/(1000*60*60);
			if(h>0){
				buf.append(h).append("时");
			}
			
			time = time%(1000*60*60);
			long m = time/(1000*60);
			if(h>0||m>0){
				buf.append(m).append("分");
			}
			
			time = time%(1000*60);
			long s = time/1000;
			if(h>0||m>0||s>0){
				buf.append(s).append("秒");
			}
			
			long mil = time%1000;
			if(h>0||m>0||s>0||mil>0){
				buf.append(s).append("毫秒");
			}
			
			return buf.toString();
		}catch(Exception e){
			return null;
		}
	}
	
	public static String dateFormat(Function function,Map<String,Object> params){
		Map<String,String> formatNames = new HashMap<String,String>();
		formatNames.put("/", "yyyy/MM/dd");
		formatNames.put("-","yyyy-MM-dd");
		formatNames.put("cn","yyyy年M月d日");
		
		return doFormat(function,params,CollectionService.dateFormat,formatNames);
	}
	
	public static String timestampFormat(Function function,Map<String,Object> params){
		Map<String,String> formatNames = new HashMap<String,String>();
		formatNames.put("/", "yyyy/MM/dd HH:mm:ss");
		formatNames.put("-","yyyy-MM-dd HH:mm:ss");
		formatNames.put("cn","yyyy年M月d日 HH:mm:ss");
		
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
						fs = "yyyy年M月d日 HH:mm:ss";
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
		
		String s = "D'2003骞�11鏈�30鏃� 12鏃�21鍒�22绉�'.df('/')";
		s = "d.time";
		Object u = ArithmeticExpression.execute(s,params);
		
		System.out.println("u : " + u);
		
		DateFormat format = new SimpleDateFormat("yyyy骞碝M鏈坉d鏃� HH:mm:ss");
		String ss = format.format(new Date());
		System.out.println(ss);
		
		
		Date d1 = new Date();
		Date d2 = new Date(System.currentTimeMillis()-1900);
		
		long time = 999999999;
		long d = time/(1000*60*60*24);
		//time = time%(1000*60*60*24);
				
		long h = time/(1000*60*60);
		time = time%(1000*60*60);
		
		long m = time/(1000*60);
		time = time%(1000*60);
		
		long sec = time/1000;
		long mil = time%1000;
		
		s = "hf(2837469)";
		u = AE.execute(s);
		
		System.out.println(u);
		//System.out.println(d1.getTime()-d2.getTime());
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
