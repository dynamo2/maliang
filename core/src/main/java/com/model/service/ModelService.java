package com.model.service;

import java.util.List;

import com.model.data.Model;

public interface ModelService {
	public void save(Model obj);
	
    public List<Model> finds();
	
    public Model get(Model obj);
    
    public Model get(String id);
}
