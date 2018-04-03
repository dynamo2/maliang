package com.maliang.core.exception;

import java.util.Map;

import com.maliang.core.model.Business;
import com.maliang.core.model.Workflow;
import com.maliang.core.service.BusinessService;


public class TurnToPage extends RuntimeException{
	private static final long serialVersionUID = 1L;
	
	private final Map<String,Object> params;
	private final Workflow flow;
	private final Business business;
	
	private BusinessService businessService = new BusinessService();

	private Object result;
	
	public TurnToPage(Map<String,Object> p){
		super();
		
		this.params = p;
		
		this.business = businessService.readBusiness(params);
		this.flow = businessService.readWorkflow(this.business,params);
	}
	
	public Workflow getWorkflow(){
		return this.flow;
	}
	
	public Map<String,Object> getParams(){
		return this.params;
	}
	
	public Business getBusiness(){
		return this.business;
	}
	
	public Object getResult(){
		return result;
	}
}
