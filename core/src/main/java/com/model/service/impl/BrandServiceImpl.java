package com.model.service.impl;

import com.model.dao.BrandDao;
import com.model.dao.impl.BrandDaoImpl;
import com.model.data.Brand;
import com.model.service.BrandService;

public class BrandServiceImpl extends ServiceImpl<Brand> implements BrandService {
	private BrandDao brandDao;
	public BrandServiceImpl() {
		this.brandDao = new BrandDaoImpl();
		
		this.setDao(this.brandDao);
	}
}
