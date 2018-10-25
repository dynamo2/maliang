package com.model.dao.impl;

import java.util.List;

import com.model.dao.DictDao;
import com.model.data.Dict;
import com.model.db.BsonUtil;
import com.model.service.Pager;

public class DictDaoImpl extends DaoImpl<Dict> implements DictDao {
	public DictDaoImpl() {
		super(Dict.class);
	}
	
	public List<Dict> finds(Pager page){
		try {
			Dict query = new Dict();
			return this.finds(BsonUtil.toBson(query),BsonUtil.toBson(query), page);
		} catch (Exception e) {
			return null;
		}
	}
}
