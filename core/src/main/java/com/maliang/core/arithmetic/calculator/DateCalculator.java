package com.maliang.core.arithmetic.calculator;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.maliang.core.arithmetic.node.Operator;

public class DateCalculator {
	public final static DateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
	
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
				ds = readString(ds);
				return dateFormat.parse(ds);
			} catch (Exception e) {}
		}

		return null;
	}
	
	private static String readString(String str){
		if(str == null)return null;
		if(str.trim().isEmpty())return "";
		
		str = str.trim();
		if(str.startsWith("'") && str.endsWith("'")){
			return str.substring(1,str.length()-1);
		}
		return str;
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
