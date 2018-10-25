package com.model.dao;

import java.util.List;

import com.model.data.Admin;
import com.model.service.Pager;

public interface AdminDao extends Dao<Admin>{
	public List<Admin> finds(Pager page);
}
