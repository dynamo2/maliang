package com.model.dao.impl;

import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.model.dao.VendorDao;
import com.model.data.DeviceModel;
import com.model.data.Vendor;
import com.model.db.BsonUtil;
import com.model.service.Pager;

public class VendorDaoImpl extends DaoImpl<Vendor> implements VendorDao {
	public VendorDaoImpl() {
		super(Vendor.class);
	}
	
	public List<Vendor> finds(Pager page){
		try {
			DeviceModel query = new DeviceModel();
			return this.finds(BsonUtil.toBson(query),BsonUtil.toBson(query), page);
		} catch (Exception e) {
			return null;
		}
	}
}
