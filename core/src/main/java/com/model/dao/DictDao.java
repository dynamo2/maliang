package com.model.dao;

import java.util.List;

import com.model.data.Dict;
import com.model.service.Pager;

public interface DictDao extends Dao<Dict>{
	public List<Dict> finds(Pager page);
}
