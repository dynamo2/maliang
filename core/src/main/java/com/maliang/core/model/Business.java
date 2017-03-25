package com.maliang.core.model;

import java.util.List;

public class Business extends MongodbModel {
	private String name;
	private String uniqueCode;
	
	//@Linked
	//private Project project;
	
	@VariableLinked
	private MongodbModel project;
	
	@Mapped(type=Workflow.class)
	private List<Workflow> workflows;
	
	@Mapped(type=Block.class)
	private List<Block> blocks;
	
	@Mapped(type=HtmlTemplate.class)
	private List<HtmlTemplate> htmlTemplates;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Workflow> getWorkflows() {
		return workflows;
	}
	public void setWorkflows(List<Workflow> workFlows) {
		this.workflows = workFlows;
	}
	public String getUniqueCode() {
		return uniqueCode;
	}
	public void setUniqueCode(String uniqueCode) {
		this.uniqueCode = uniqueCode;
	}
	public List<Block> getBlocks() {
		return blocks;
	}
	public void setBlocks(List<Block> blocks) {
		this.blocks = blocks;
	}
//	public Project getProject() {
//		return project;
//	}
//	public void setProject(Project project) {
//		this.project = project;
//	}
	public MongodbModel getProject() {
		return project;
	}
	public void setProject(MongodbModel project) {
		this.project = project;
	}
	public List<HtmlTemplate> getHtmlTemplates() {
		return htmlTemplates;
	}
	public void setHtmlTemplates(List<HtmlTemplate> htmlTemplates) {
		this.htmlTemplates = htmlTemplates;
	}
	public Workflow workFlow(int step){
		if(this.workflows == null || this.workflows.isEmpty()){
			return null;
		}
		
		for(Workflow wf : this.workflows){
			if(wf == null || wf.getStep() == null){
				continue;
			}
			
			if(wf.getStep() == step){
				return wf;
			}
		}
		
		return this.workflows.get(0);
	}
} 
