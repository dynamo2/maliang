package com.model.dao;

import java.util.List;

import com.model.data.Brand;
import com.model.service.Pager;

public interface BrandDao extends Dao<Brand>{
	public List<Brand> finds(Pager page);
}
