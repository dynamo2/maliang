package com.maliang.core.service;

import java.util.List;

import com.maliang.core.dao.ObjectMetadataDao;
import com.maliang.core.model.ObjectMetadata;

public class ObjectMetadataService {
	private ObjectMetadataDao dao = new ObjectMetadataDao();
	
	public List<ObjectMetadata> list(){
		return dao.list();
	}
}
