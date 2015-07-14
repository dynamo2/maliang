package com.maliang.core.service;

import java.util.Map;

import com.maliang.core.arithmetic.ArithmeticExpression;
import com.maliang.core.dao.BusinessDao;
import com.maliang.core.model.Business;
import com.maliang.core.model.WorkFlow;

public class BusinessService {
	BusinessDao businessDao = new BusinessDao();
	
	public Object business(Map<String,Object> params){
		String key = (String)MapHelper.readValue(params,"bid");
		Business business = null;
		if(key != null && !key.isEmpty()){
			business = this.businessDao.getByID(key);
		}
		
		if(business == null){
			key = (String)MapHelper.readValue(params,"bn");
			business = this.businessDao.getByName(key);
		}
		
		int fid = (Integer)MapHelper.readValue(params,"fid",-1);
		WorkFlow flow = business.workFlow(fid);
		
		ArithmeticExpression.execute(flow.getCode(), params);
		
		return ArithmeticExpression.execute(flow.getResponse(), params);
	}
}
