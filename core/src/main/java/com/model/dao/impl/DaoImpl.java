package com.model.dao.impl;

import java.lang.reflect.Method;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.model.dao.Dao;
import com.model.db.BsonUtil;
import com.model.db.MongoDB;
import com.model.db.MongoObject;
import com.model.db.MongoSession;
import com.model.service.Pager;

public class DaoImpl<T> implements Dao<T>{
	protected MongoSession session;
	
	public DaoImpl(Class<T> modelClass) {
		MongoObject mongoObject = new MongoObject();
        MongoDB demo1 = new MongoDB(mongoObject, "tianma");
        
        this.session = new MongoSession(modelClass,demo1.excute());
	}
	
	public static <T> DaoImpl<T> create(Class<T> clazz){
		return new DaoImpl<T>(clazz);
	}
	
	public static <T> T get(Class<T> clazz,Object bid){
		DaoImpl<T> dao = create(clazz);
    	try {
    		ObjectId id = null;
    		if(bid instanceof ObjectId) {
    			id = (ObjectId)bid;
    		}else {
    			id = new ObjectId(bid.toString());
    		}

    		return dao.get(id);
    	}catch(IllegalArgumentException e) {
    		return null;
    	}
	}
	
	public void save(T obj) {
		if(this.hasId(obj)) {
			this.session.update(obj);
		}else {
			this.session.save(obj);
		}
	}
	
	public boolean hasId(T obj) {
		Method[] linkedMs = obj.getClass().getDeclaredMethods();
    	for(Method m : linkedMs) {
    		if("getId".equals(m.getName())) {
				try {
					Object id = m.invoke(obj);
					return id != null && (id instanceof ObjectId);
				} catch (Exception e) {}
				
				return false;
    			
    		}
    	}
    	return false;
	}
	
    public List<T> finds() {
        return (List<T>)this.session.finds();
    }
    
    public List<T> finds(T obj) {
		try {
			Document doc = BsonUtil.toBson(obj);
			return (List<T>)this.session.query(doc);
		} catch (Exception e) {
			return null;
		}   
    }
    
    public List<T> finds(Bson bson, Bson sort) {
    	return (List<T>)this.session.query(bson, sort);
    }
    
    public List<T> finds(Bson bson, Bson sort, Pager page) {
    	return (List<T>)this.session.query(bson, sort, page);
    }
    
    public List<T> finds(Bson bson, Bson sort, int limit, int skip) {
    	return (List<T>)this.session.query(bson, sort, limit, skip);
    }
	
    public T get(T obj) {
        return (T)this.session.find(obj);
    }
    
    public T get(ObjectId id) {
        return (T)this.session.get(id);
    }
    
    public T get(String id) {
        return (T)this.session.get(id);
    }
}
