package com.model.service.impl;

import com.model.dao.AdminDao;
import com.model.dao.impl.AdminDaoImpl;
import com.model.data.Admin;
import com.model.service.AdminService;

public class AdminServiceImpl extends ServiceImpl<Admin> implements AdminService {
	private AdminDao adminDao;
	public AdminServiceImpl() {
		this.adminDao = new AdminDaoImpl();
		
		this.setDao(this.adminDao);
	}
}
