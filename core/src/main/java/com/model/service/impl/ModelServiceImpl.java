package com.model.service.impl;

import com.maliang.core.dao.ModelDao;
import com.model.dao.impl.ModelDaoImpl;
import com.model.data.Model;
import com.model.service.ModelService;

public class ModelServiceImpl extends ServiceImpl<Model> implements ModelService {
	private ModelDao modelDao;
	public ModelServiceImpl() {
//		this.modelDao = new ModelDaoImpl();
//		
//		this.setDao(this.modelDao);
	}
}
