package com.maliang.core.arithmetic.function;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.maliang.core.arithmetic.AE;
import com.maliang.core.arithmetic.ArithmeticExpression;
import com.maliang.core.service.CollectionService;
import com.maliang.core.service.MapHelper;
import com.maliang.core.util.Utils;

public class DateFunction {
	public static final int MATCH_DATE = 1;
	public static final int MATCH_TIME = 2;
	public static final int MATCH_DATETIME = 0;
	
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
		}else if(fp != null){
			try {
				String fs = formatNames.get(fp.toString());
				if(fs == null) {
					fs = fp.toString();
				}
				
				if(fs != null){
					format = new SimpleDateFormat(fs);
				}
			}catch(Exception e){
				format = defaultFormat;
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
	
	/**
	 * 将完整的日期时间部分数据归零
	 * MATCH_TIME：匹配时间，将日期归零，设定为初始日期：1970-01-01
	 * MATCH_DATE：匹配日期，将时间归零，设定为：00:00:00.000
	 * ***/
	private static void toZero(Calendar c,int modal){
		if(modal == MATCH_TIME) {
			c.set(Calendar.YEAR,1970);
			c.set(Calendar.MONTH,0);
			c.set(Calendar.DATE,1);
		}else if(modal == MATCH_DATE) {
			c.set(Calendar.HOUR_OF_DAY,0);
			c.set(Calendar.MINUTE,0);
			c.set(Calendar.SECOND,0);
			c.set(Calendar.MILLISECOND,0);
		}
	}
	
	public static int readMatchModal(Object options){
		Object mVal = MapHelper.readValue(options,"flag");
		if(mVal == null) {
			return MATCH_DATETIME;
		}
		
		if("d".equals(mVal) || "date".equals(mVal)) {
			return MATCH_DATE;
		}
		
		if("t".equals(mVal) || "time".equals(mVal)) {
			return MATCH_TIME;
		}
		return MATCH_DATETIME;
	}
	
	/***
	 * example: 
	 * 1. 'D2018-01-03'.between({
	 * 		date:['2018-01-01','2018-01-06'],
	 * 		flag:'date'
	 * })
	 * 
	 * 2. 'D13:34:22'.between({
	 * 		date:[
	 * 			['10:00:00','14:00:00'],
	 * 			['23:00:00','02:00:00']
	 * 		],
	 * 		flag:'time'
	 * })
	 * **/
	public static boolean between(Object date,Object options){
		if(options instanceof List) {
			return between(date,(List)options,0);
		}
		
		if(options instanceof Map) {
			Integer modal = readMatchModal(options);
			
			Object mVal = MapHelper.readValue(options,"date");
			if(mVal instanceof List) {
				return between(date,(List)mVal,modal);
			}
			
			return between(Utils.parseDate(date),Utils.parseDate(mVal),null,modal);
		}
		
		return false;
	}
	
	private static boolean between(Object date,List<Object> list,int modal){
		date = Utils.parseDate(date);
		if(date == null || Utils.isEmpty(list)) {
			return false;
		}

		Date min = null;
		Date max = null;
		for(Object o : list) {
			if(o == null) {
				continue;
			}
			
			if(o instanceof List) {
				if(between((Date)date,(List)o,modal)) {
					return true;
				}
				continue;
			}
			
			Date temp = null;
			if(o instanceof Date) {
				temp = (Date)o;
			}else {
				temp = Utils.parseDate(o.toString());
			}
			
			if(min == null) {
				min = temp;
			}else {
				max = temp;
				break;
			}
		}
		
		return between((Date)date,min,max,modal);
	}
	
	private static Calendar toCalendar(Date time,int modal){
		if(time == null) {
			return null;
		}
		
		Calendar c = Calendar.getInstance();
		c.setTime(time);
		toZero(c,modal);
		return c;
	}
	
	private static boolean between(Date date,Date min,Date max,int modal){
		if(date == null || min == null) {
			return false;
		}
		
		Calendar cDate = toCalendar(date,modal);
		Calendar cMin = toCalendar(min,modal);
		Calendar cMax = toCalendar(max,modal);
		
		if(cMax == null) {
			return cDate.compareTo(cMin) == 0;
		}
		
		/**
		 * '01:22:42' match ['23:00:00','02:00:00']
		 * **/
		if(modal == MATCH_TIME && cMin.after(cMax)) {
			return cDate.compareTo(cMin) >= 0 || cDate.compareTo(cMax) <= 0;
		}
		
		return cDate.compareTo(cMin) >= 0 && cDate.compareTo(cMax) <= 0;
	}
	
	private static void testBetween() {
		System.out.println("AE.execute(\"date('2019-01-01')\") : " + AE.execute("date('2019-01-01')"));
		System.out.println("AE.execute(\"date('13:32:23')\") : " + AE.execute("date('13:32:23')"));
		System.out.println("between : " + AE.execute("D'13:32:23'.between({date:[[D'10:00:00',D'15:00:00'],[D'14:00:00',D'16:00:00']],flag:'time'})"));
		System.out.println("between : " + AE.execute("D'13:32:23'.between({date:[[D'14:00:00',D'16:00:00']],flag:'time'})"));
		System.out.println("between : " + AE.execute("D'13:32:23'.between({date:[[D'23:00:00',D'10:00:00']],flag:'time'})"));
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
		
		Date date = new Date();
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		Calendar cd = Calendar.getInstance();
		cd.setTime(date);
		
		System.out.println("date 1 : " + df.format(cd.getTime())+", " + cd.getTimeInMillis());
		cd.set(Calendar.YEAR,1970);
		cd.set(Calendar.MONTH,0);
		cd.set(Calendar.DATE,1);
		
		System.out.println("date 2 : " +  df.format(cd.getTime())+", " + cd.getTimeInMillis());
		
		
		cd.set(Calendar.HOUR_OF_DAY,0);
		cd.set(Calendar.MINUTE,0);
		cd.set(Calendar.SECOND,0);
		
		System.out.println("date 3 : " +  df.format(cd.getTime())+", " + cd.getTimeInMillis());
		
		cd.set(Calendar.MILLISECOND,0);
		
		System.out.println("date 4 : " +  df.format(cd.getTime())+", " + cd.getTimeInMillis());
		
		
		Calendar cd2 = Calendar.getInstance();
		
		cd2.set(Calendar.YEAR,1970);
		cd2.set(Calendar.MONTH,0);
		cd2.set(Calendar.DATE,1);
		cd2.set(Calendar.HOUR_OF_DAY,0);
		cd2.set(Calendar.MINUTE,0);
		cd2.set(Calendar.SECOND,0);
		cd2.set(Calendar.MILLISECOND,0);
		
		System.out.println("cd bi cd2 : " + cd.compareTo(cd2)+", after : " + cd.after(cd2));
		System.out.println("date 4 : " +  df.format(cd2.getTime())+", " + cd2.getTimeInMillis());
		System.out.println("date  : " +  df.format(date));
		
		
	}
	
}
