package com.model.service.impl;

import java.util.List;

import org.bson.conversions.Bson;

import com.model.dao.Dao;
import com.model.service.Pager;
import com.model.service.Service;

public class ServiceImpl<T> implements Service<T> {
	private Dao dao;
	
	public void setDao(Dao d) {
		this.dao = d;
	}
	
	public void save(T obj) {
	    this.dao.save(obj);
	}
	
    public List<T> finds() {
        return (List<T>)this.dao.finds();
    }
    
    public List<T> finds(T obj) {
        return (List<T>)this.dao.finds(obj);
    }
    
    public List<T> finds(Pager page) {
        return (List<T>)this.dao.finds();
    }
	
    public T get(T obj) {
        return (T)this.dao.get(obj);
    }
    
    public T get(String id) {
        return (T)this.dao.get(id);
    }
    
    public List<T> finds(Bson bson, Bson sort) {
    	return this.dao.finds(bson, sort);
    }
}
