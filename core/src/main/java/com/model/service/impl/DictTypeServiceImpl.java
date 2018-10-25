package com.model.service.impl;

import com.model.dao.DictTypeDao;
import com.model.dao.impl.DictTypeDaoImpl;
import com.model.data.DictType;
import com.model.service.DictTypeService;

public class DictTypeServiceImpl extends ServiceImpl<DictType> implements DictTypeService {
	private DictTypeDao dictTypeDao;
	public DictTypeServiceImpl() {
		this.dictTypeDao = new DictTypeDaoImpl();
		
		this.setDao(this.dictTypeDao);
	}
}
