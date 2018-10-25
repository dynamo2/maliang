package com.model.dao;

import java.util.List;

import com.model.data.DeviceModel;
import com.model.service.Pager;

public interface DeviceModelDao extends Dao<DeviceModel>{
	public List<DeviceModel> finds(Pager page);
}
