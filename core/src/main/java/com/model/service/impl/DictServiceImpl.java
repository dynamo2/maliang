package com.model.service.impl;

import com.model.dao.DictDao;
import com.model.dao.impl.DictDaoImpl;
import com.model.data.Dict;
import com.model.service.DictService;

public class DictServiceImpl extends ServiceImpl<Dict> implements DictService {
	private DictDao dictDao;
	public DictServiceImpl() {
		this.dictDao = new DictDaoImpl();
		
		this.setDao(this.dictDao);
	}
}
