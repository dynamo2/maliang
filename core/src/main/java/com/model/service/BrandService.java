package com.model.service;

import java.util.List;

import com.model.data.Brand;

public interface BrandService {
	public void save(Brand obj);
	
    public List<Brand> finds();
	
    public Brand get(Brand obj);
    
    public Brand get(String id);
}
