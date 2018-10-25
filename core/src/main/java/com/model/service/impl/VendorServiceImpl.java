package com.model.service.impl;

import com.model.dao.VendorDao;
import com.model.dao.impl.VendorDaoImpl;
import com.model.data.Vendor;
import com.model.service.VendorService;

public class VendorServiceImpl extends ServiceImpl<Vendor> implements VendorService {
	private VendorDao vendorDao;
	public VendorServiceImpl() {
		this.vendorDao = new VendorDaoImpl();
		
		this.setDao(this.vendorDao);
	}
}
