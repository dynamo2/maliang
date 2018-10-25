package com.model.dao.impl;

import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.model.dao.DeviceModelDao;
import com.model.data.DeviceModel;
import com.model.db.BsonUtil;
import com.model.service.Pager;

public class DeviceModelDaoImpl extends DaoImpl<DeviceModel> implements DeviceModelDao {
	public DeviceModelDaoImpl() {
		super(DeviceModel.class);
	}
	
	public List<DeviceModel> finds(Pager page){
		try {
			DeviceModel query = new DeviceModel();
			return this.finds(BsonUtil.toBson(query),BsonUtil.toBson(query), page);
		} catch (Exception e) {
			return null;
		}
	}
	
	
	
	public static void main(String[] args) {
		DeviceModelDaoImpl dao = new DeviceModelDaoImpl();
		
		Pager page = new Pager();
		DeviceModel q1 = new DeviceModel();
		List<DeviceModel> list;
		try {
			page.setCurPage(11);
			list = dao.finds(BsonUtil.toBson(q1),BsonUtil.toBson(q1), page);
			
			System.out.println("---- list size : " + list.size());
			for(DeviceModel mt : list) {
				System.out.println(mt.getId()+" - "+mt.getName() +"-- type : " + mt.getType());
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		} 
		
		//List<DeviceModel> list = dao.finds();
//		System.out.println("---- list size : " + list.size());
//		for(DeviceModel mt : list) {
//			System.out.println(mt.getId()+" - "+mt.getName() +"-- type : " + mt.getType());
//		}
		
		DeviceModel query = new DeviceModel();
		query.setId(new ObjectId("5b1f34b49f7b0317f386ccf1"));
		//query.setName("水景");

		try {
			Document doc = BsonUtil.toBson(query);
			System.out.println("doc : " + doc);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		DeviceModel type = dao.get(query);
		System.out.println(type.getName());
	}
}
