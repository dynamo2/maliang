package com.model.dao;

import java.util.List;

import com.model.data.Vendor;
import com.model.service.Pager;

public interface VendorDao extends Dao<Vendor>{
	public List<Vendor> finds(Pager page);
}
