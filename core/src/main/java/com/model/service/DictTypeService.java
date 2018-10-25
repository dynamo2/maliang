package com.model.service;

import java.util.List;

import com.model.data.DictType;

public interface DictTypeService {
	public void save(DictType obj);
	
    public List<DictType> finds();
	
    public DictType get(DictType obj);
    
    public DictType get(String id);
}
