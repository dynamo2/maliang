package com.model.dao.impl;

import java.util.List;

import org.bson.Document;

import com.model.dao.AdminDao;
import com.model.data.Admin;
import com.model.service.Pager;

public class AdminDaoImpl extends DaoImpl<Admin> implements AdminDao {
	public AdminDaoImpl() {
		super(Admin.class);
	}
	
	public List<Admin> finds(Pager page){
		try {
			Admin query = new Admin();
			return this.finds(new Document(),new Document(), page);
		} catch (Exception e) {
			return null;
		}
	}
}
