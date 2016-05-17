package com.maliang.core.dao;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.maliang.core.model.FieldType;

public class DaoHelper {
	public final static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	public final static DateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public final static DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
	
	public static Object correctFieldValue(int ftype,Object value){
		if(FieldType.DOUBLE.is(ftype)){
			try {
				return Double.valueOf(value.toString().trim());
			}catch(Exception e){
				return null;
			}
		}
		
		if(FieldType.INT.is(ftype)){
			try {
				return Integer.valueOf(value.toString().trim());
			}catch(Exception e){
				return null;
			}
		}
		
		if(FieldType.DATE.is(ftype)){
			try {
				return timestampFormat.parse(value.toString().trim());
			}catch(ParseException e){
				try {
					return dateFormat.parse(value.toString().trim());
				}catch(ParseException ee){
					return null;
				}
			}
		}
		
		if(FieldType.STRING.is(ftype)){
			try {
				return value.toString();
			}catch(Exception e){
				return null;
			}
		}

		return value;
	}
}
