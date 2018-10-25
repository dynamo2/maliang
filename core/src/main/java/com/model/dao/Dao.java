package com.model.dao;

import java.util.List;

import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.model.service.Pager;

public interface Dao<T> {
	public void save(T obj);
	
	public List<T> finds();
	
	public List<T> finds(T obj);
	
    public T get(T obj);
    
    public T get(ObjectId id);
    
    public T get(String id);
    
    public List<T> finds(Bson bson, Bson sort, int limit, int skip);
    
    public List<T> finds(Bson bson, Bson sort,Pager page);
    
    public List<T> finds(Bson bson, Bson sort);
}
