package com.model.service;

import java.util.List;

import org.bson.conversions.Bson;

public interface Service<T> {
	public void save(T obj);
	
    public List<T> finds();
    
    public List<T> finds(T obj);
    
    public List<T> finds(Bson bson, Bson sort);
	
    public T get(T obj);
    
    public T get(String id);
}
