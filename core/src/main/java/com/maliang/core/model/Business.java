package com.maliang.core.model;

import java.util.List;

public class Business extends MongodbModel {
	private String name;
	private List<WorkFlow> workFlows;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<WorkFlow> getWorkFlows() {
		return workFlows;
	}
	public void setWorkFlows(List<WorkFlow> workFlows) {
		this.workFlows = workFlows;
	}
	
	public WorkFlow workFlow(int step){
		if(this.workFlows == null || this.workFlows.isEmpty()){
			return null;
		}
		
		for(WorkFlow wf : this.workFlows){
			if(wf.getStep() == step){
				return wf;
			}
		}
		
		return this.workFlows.get(0);
	}
} 
