package com.model.dao.impl;

import java.util.List;

import com.model.dao.ModelDao;
import com.model.data.DeviceModel;
import com.model.data.Model;
import com.model.db.BsonUtil;
import com.model.service.Pager;

public class ModelDaoImpl extends DaoImpl<Model> implements ModelDao {
	public ModelDaoImpl() {
		super(Model.class);
	}
	
	public List<Model> finds(Pager page){
		try {
			DeviceModel query = new DeviceModel();
			return this.finds(BsonUtil.toBson(query),BsonUtil.toBson(query), page);
		} catch (Exception e) {
			return null;
		}
	}
}
