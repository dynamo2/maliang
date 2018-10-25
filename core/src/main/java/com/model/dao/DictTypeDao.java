package com.model.dao;

import java.util.List;

import com.model.data.DictType;
import com.model.service.Pager;

public interface DictTypeDao extends Dao<DictType>{
	public List<DictType> finds(Pager page);
}
