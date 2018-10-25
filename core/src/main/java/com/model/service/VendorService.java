package com.model.service;

import java.util.List;

import com.model.data.ModelType;
import com.model.data.Vendor;

public interface VendorService {
	public void save(Vendor obj);
	
    public List<Vendor> finds();
	
    public Vendor get(Vendor obj);
    
    public Vendor get(String id);
}
