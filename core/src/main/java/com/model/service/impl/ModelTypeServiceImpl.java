package com.model.service.impl;

import com.model.dao.ModelTypeDao;
import com.model.dao.impl.ModelTypeDaoImpl;
import com.model.data.ModelType;
import com.model.service.ModelTypeService;

public class ModelTypeServiceImpl extends ServiceImpl<ModelType> implements ModelTypeService {
	private ModelTypeDao typeDao;
	public ModelTypeServiceImpl() {
		this.typeDao = new ModelTypeDaoImpl();
		
		this.setDao(this.typeDao);
	}
}
