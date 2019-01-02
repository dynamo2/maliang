package com.maliang.core.dao;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import com.maliang.core.model.FieldType;
import com.mongodb.BasicDBObject;

public class DaoHelper {
	public final static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	public final static DateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public final static DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
	
	public static void main(String[] args) {
		
		try {
			Date date = dateFormat.parse("2018-11-01");
			System.out.println("--- date : " + date.toGMTString());
		}catch(ParseException ee){
			ee.printStackTrace();
		}
	}
	
	public static Object correctFieldValue(int ftype,Object value){
		if(FieldType.DOUBLE.is(ftype)){
			if(value instanceof Double){
				return (Double)value;
			}
			
			try {
				return Double.valueOf(value.toString().trim());
			}catch(Exception e){
				return null;
			}
		}
		
		if(FieldType.INT.is(ftype)){
			if(value instanceof Integer){
				return (Integer)value;
			}
			
			try {
				return Integer.valueOf(value.toString().trim());
			}catch(Exception e){
				return null;
			}
		}
		
		if(FieldType.DATE.is(ftype)){
			if(value instanceof Date){
				return (Date)value;
			}

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
	
	public static Map<String,Object> emptyResult(){
		return new HashMap<String,Object>();
	}
	
	public static BasicDBObject dbQuery(Map query){
		//toDBVal(query);
		
		if(query == null){
			return new BasicDBObject();
		}
		if(query instanceof BasicDBObject){
			return (BasicDBObject)query;
		}
		
		return new BasicDBObject(query);
	}
	
	private static void toDBVal(Map<String,Object> map){
		if(map == null)return;
		
		Map<String,Map<String,Object>> rms = new HashMap<String,Map<String,Object>>();
		for(String k : map.keySet()){
			Object val = map.get(k);
			
			if(val instanceof Map){
				toDBVal((Map<String,Object>)val);
				continue;
			}
			
			if(val instanceof List){
				for(Object lv:(List)val){
					if(lv instanceof Map){
						toDBVal((Map<String,Object>)lv);
					}
				}
				continue;
			}
			
			/**
			 * switch id To ObjectId
			 * **/
			if(k.equals("id") || k.equals("_id") || k.endsWith(".id") || k.endsWith("._id")){
				val = getObjectId(val,true);
				
				
				if(!k.endsWith("_id")){
					String nk = k.replace("id","_id");
					Map<String,Object> np = new HashMap<String,Object>();
					np.put(nk,val);
					rms.put(k,np);
				}else {
					map.put(k,val);
				}
			}
		}
		
		for(String rk:rms.keySet()){
			Map<String,Object> val = rms.get(rk);
			map.remove(rk);
			map.putAll(val);
		}
	}
	
	public static ObjectId getObjectId(Object oid,Boolean hasDefault){
		if(oid != null && oid instanceof ObjectId){
			return (ObjectId)oid;
		}
		
		try {
			return new ObjectId(oid.toString());
		}catch(IllegalArgumentException e){
			if(hasDefault){
				return new ObjectId();
			}
			
			return null;
		}
	}
	
	public static BasicDBObject getObjectIdQuery(Object oid){
		if(oid != null && oid instanceof ObjectId){
			return new BasicDBObject("_id",(ObjectId)oid);
		}
		
		try {
			return new BasicDBObject("_id",new ObjectId(oid.toString()));
		}catch(IllegalArgumentException e){
			return new BasicDBObject("_id",new ObjectId());
		}
	}
}
