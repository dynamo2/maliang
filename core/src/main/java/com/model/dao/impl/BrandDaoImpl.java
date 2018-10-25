package com.model.dao.impl;

import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.model.dao.BrandDao;
import com.model.data.Brand;
import com.model.data.DeviceModel;
import com.model.db.BsonUtil;
import com.model.service.Pager;

public class BrandDaoImpl extends DaoImpl<Brand> implements BrandDao {
	public BrandDaoImpl() {
		super(Brand.class);
	}
	
	public List<Brand> finds(Pager page){
		try {
			DeviceModel query = new DeviceModel();
			return this.finds(BsonUtil.toBson(query),BsonUtil.toBson(query), page);
		} catch (Exception e) {
			return null;
		}
	}
}
