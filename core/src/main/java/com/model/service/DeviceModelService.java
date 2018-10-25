package com.model.service;

import java.util.List;
import java.util.Map;

import org.bson.conversions.Bson;

import com.model.data.DeviceModel;

public interface DeviceModelService {
public void save(DeviceModel obj);
	
    public List<DeviceModel> finds();
	
    public DeviceModel get(DeviceModel obj);
    
    public DeviceModel get(String id);
    
    public List<DeviceModel> finds(Bson bson, Bson sort, int limit, int skip);
    
    public List<DeviceModel> finds(Pager page);
    
    public List<DeviceModel> finds(Pager page,Map map);
    
    public List<DeviceModel> finds(Map<String,Object> search,Map<String,Object> not,Pager page);
}
