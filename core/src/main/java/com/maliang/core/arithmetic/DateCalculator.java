package com.maliang.core.arithmetic;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateCalculator {
	public final static DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
	
	public static Date calculate(Date date,String increment,Operator operator){
		if(!operator.isPlus() && !operator.isSubstruction()){
			throw new RuntimeException("Error operator '"+operator.getOperatorKey()+"' for Date");
		}
		
		if(!isDateIncrement(increment)){
			throw new RuntimeException("Error parameter '"+increment+"' for Date");
		}
		
		Calendar cal = Calendar.getInstance();  
		cal.setTime(date);
		char flagChar = increment.charAt(increment.length()-1);
		int incre = new Integer(increment.substring(0,increment.length()-1));
		incre = operator.isPlus() ? incre:-incre;
		
		if(flagChar == 'y'){
			cal.add(Calendar.YEAR,incre);
		}else if(flagChar == 'm'){
			cal.add(Calendar.MONTH,incre);
		}else if(flagChar == 'd'){
			cal.add(Calendar.DATE,incre);
		}else if(flagChar == 'h'){
			cal.add(Calendar.HOUR_OF_DAY,incre);
		}else if(flagChar == 'i'){
			cal.add(Calendar.MINUTE,incre);
		}else if(flagChar == 's'){
			cal.add(Calendar.SECOND,incre);
		}

		return cal.getTime();
	}
	
	public static Date readDate(String source){
		if(source == null || source.isEmpty()){
			return null;
		}
		
		source = source.trim();
		if(source.startsWith("D")){
			try {
				String ds = source.substring(1,source.length());
				
				return dateFormat.parse(ds);
			} catch (ParseException e) {}
		}

		return null;
	}
	
	public static boolean isDateIncrement(String source){
		if(source == null || source.isEmpty()){
			return false;
		}
		
		source = source.trim();
		if(source.length() < 2){
			return false;
		}
		
		char lastChar = source.charAt(source.length()-1);
		if(lastChar == 'd' 
			|| lastChar == 'y'
			|| lastChar == 'm'
			|| lastChar == 'h'
			|| lastChar == 'i'
			|| lastChar == 's'){
			
			try {
				String str = source.substring(0, source.length()-1);
				new Integer(str);
				return true;
			}catch(Exception e){}
		}
		return false;
	}
}
