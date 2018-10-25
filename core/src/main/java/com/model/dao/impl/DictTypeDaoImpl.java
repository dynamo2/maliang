package com.model.dao.impl;

import java.util.List;

import com.model.dao.DictTypeDao;
import com.model.data.DictType;
import com.model.db.BsonUtil;
import com.model.service.Pager;

public class DictTypeDaoImpl extends DaoImpl<DictType> implements DictTypeDao {
	public DictTypeDaoImpl() {
		super(DictType.class);
	}
	
	public List<DictType> finds(Pager page){
		try {
			DictType query = new DictType();
			return this.finds(BsonUtil.toBson(query),BsonUtil.toBson(query), page);
		} catch (Exception e) {
			return null;
		}
	}
}
