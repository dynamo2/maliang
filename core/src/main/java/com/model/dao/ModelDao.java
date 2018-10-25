package com.model.dao;

import java.util.List;

import com.model.data.Model;
import com.model.service.Pager;

public interface ModelDao extends Dao<Model>{
	public List<Model> finds(Pager page);
}
