package com.model.dao.impl;

import java.util.List;

import org.bson.Document;

import com.model.dao.ModelTypeDao;
import com.model.data.ModelType;
import com.model.db.BsonUtil;

public class ModelTypeDaoImpl extends DaoImpl<ModelType> implements ModelTypeDao {
	public ModelTypeDaoImpl() {
		super(ModelType.class);
	}

	public static void main(String[] args) {
		ModelTypeDaoImpl dao = new ModelTypeDaoImpl();
		
		List<ModelType> list = dao.finds();
		
		for(ModelType mt : list) {
			System.out.println(mt.getId()+" - "+mt.getName()+"---- parent : "+mt.getParent());
		}
		
		ModelType query = new ModelType();
		//query.setId(new ObjectId("5b1f64969f7b0317f386cdbf"));
		query.setName("水景");

		try {
			Document doc = BsonUtil.toBson(query);
			System.out.println("doc : " + doc);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		ModelType type = dao.get(query);
		System.out.println(type);
	}

}
