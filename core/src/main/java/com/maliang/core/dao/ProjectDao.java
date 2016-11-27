package com.maliang.core.dao;

import com.maliang.core.model.Project;

public class ProjectDao extends ModelDao<Project> {
	protected static String COLLECTION_NAME = "System";
	protected CollectionDao collectionDao = new CollectionDao();
	
	public ProjectDao(){
		super(COLLECTION_NAME,Project.class);
	}
}
 