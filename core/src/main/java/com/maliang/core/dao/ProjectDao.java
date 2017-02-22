package com.maliang.core.dao;

import org.bson.types.ObjectId;

import com.maliang.core.model.ObjectField;
import com.maliang.core.model.Project;
import com.maliang.core.model.Subproject;
import com.maliang.core.model.Trigger;
import com.maliang.core.model.TriggerAction;

public class ProjectDao extends ModelDao<Project> {
	protected static String COLLECTION_NAME = "System";
	protected CollectionDao collectionDao = new CollectionDao();
	
	static {
		INNER_TYPE.put("Project.subprojects",Subproject.class);
	}
	
	public ProjectDao(){
		super(COLLECTION_NAME,Project.class);
	}
	
	
	public Subproject getSubprojectById(String sid){
		try {
			return this.getArrayInnerById("subprojects", new ObjectId(sid),Subproject.class);
		}catch(Exception e){
			return null;
		}
	}
	
	/**
	 * omId: Project.id
	 * **/
	public void saveSubproject(String omId,Subproject subp){
		this.saveArrayInnerFields(omId, "subprojects", subp);
	}
	
	public void deleteSubproject(String sid){
		this.deleteArrayInnerFields(sid, "subprojects");
	}
}
 