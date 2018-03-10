package com.maliang.core.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import com.maliang.core.model.ModelType;
import com.maliang.core.model.ObjectMetadata;
import com.maliang.core.service.MapHelper;
import com.maliang.core.util.Utils;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

public class TreeModelDao extends BasicDao{
	
	public boolean isTreeModel(ObjectMetadata meta){
		if(meta == null || meta.getModelType() == null){
			return false;
		}
		
		return ModelType.TREE.is(meta.getModelType());
	}
	
	/**
	 * �жϽڵ�ĸ������Ƿ��б��
	 * ***/
	public boolean sameParent(Map<String,Object> oldVal,Map<String,Object> newVal){
		/***
		 * �жϽڵ�ĸ������Ƿ��иı�(oldVal.parent==newVal.parent)
		 * �ı䣺����������ӽڵ��·����_path_��
		 * δ�ı䣺������
		 * **/
		Object oldParent = MapHelper.readValue(oldVal,"_parent_.id");
		Object newParent = MapHelper.readValue(newVal,"_parent_");
		
		if(Utils.isEmpty(oldParent) && Utils.isEmpty(newParent)){
			return true;
		}
		
		if(Utils.isEmpty(oldParent)){
			return false;
		}
		
		if(Utils.isEmpty(newParent)){
			return false;
		}
		
		return oldParent.equals(newParent);
	}
	
	/***
	 * �ƶ����ṹ��ĳ�ڵ�������ӽڵ�
	 * **/
	public void updateChildrenPaths(Map<String,Object> oldVal,Map<String,Object> newVal,ObjectMetadata meta){
		if(!isTreeModel(meta) || sameParent(oldVal,newVal)){
			return;
		}
		
		/***
		 * ��ȡ·����
		 * ��·����oldVal._path_
		 * ��·����newVal._path_
		 * **/
		String oid = this.readObjectIdToString(oldVal);
		List<Object> oldPath = Utils.toList(MapHelper.readValue(oldVal,"_path_.id"),true);
		List<Object> newPath = Utils.toList(MapHelper.readValue(newVal,"_path_"),true);
		
		oldPath.add(oid);
		newPath.add(oid);
		
		System.out.println("moveChildTree oldPath : " + oldPath);
		System.out.println("moveChildTree newPath : " + newPath);
		
		/**
		 * ���ݾ�·�����Ҫ�����������ӽڵ��ID
		 * **/
		//String query = "{_path_:{$all:oldPath}}";
		BasicDBObject query = new BasicDBObject(newMap("_path_",newMap("$all",oldPath)));
		
		String collName = meta.getName();
		List<Map<String,Object>> list = this.find(query, collName);
		if(Utils.isEmpty(list)){
			return;
		}
		
		List<ObjectId> ids = new ArrayList<ObjectId>();
		for(Map<String,Object> data : list){
			ids.add(new ObjectId((String)data.get("id")));
		}
		query = this.build(newMap("_id",newMap("$in",ids)));
		
		
		/***
		 * ɾ���ӽڵ�ľ�·��
		 * **/
		//String pull = "{$pull:{_path_:{$in:oldPath}}}";
		BasicDBObject pull = new BasicDBObject(newMap("$pull", newMap("_path_",newMap("$in",oldPath))));
		
		/***
		 * ������·��
		 * **/
		//String push = "{$push:{_path_:{$each:newPath,$position:0}}}";
		Map<String,Object> pushPath = newMap("$each",newPath);
		pushPath.put("$position",0);
		BasicDBObject push = new BasicDBObject(newMap("$push",newMap("_path_",pushPath)));

		DBCollection db = this.getDBCollection(collName);
		db.updateMulti(query, pull);
		db.updateMulti(query, push);
		
		System.out.println("moveChildTree query : " + query);
		System.out.println("moveChildTree pull : " + pull);
		System.out.println("moveChildTree push : " + push);
		System.out.println("moveChildTree id query list : " + this.find(query, collName));
	}
}
