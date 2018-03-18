package com.maliang.core.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import com.maliang.core.model.FieldType;
import com.maliang.core.model.ModelType;
import com.maliang.core.model.ObjectField;
import com.maliang.core.model.ObjectMetadata;
import com.maliang.core.service.MapHelper;
import com.maliang.core.util.Utils;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

public class TreeModelDao {
	
	/***
	 * ����Treeģ�͵�ר���ֶΣ�_parent_, _path_
	 *
	protected void doTreeModel(Map val,ObjectMetadata metadata,BasicDao dao){
		if(metadata == null || !ModelType.TREE.is(metadata.getModelType())){
			return;
		}
		
		ObjectField field = dao.readObjectField(metadata.getFields(),ObjectMetadata.TREE_MODEL_PARENT_KEY);
		String parentCollection = field.getLinkedObject();
		
		Map<String,Object> parent = null;
		Object p = MapHelper.readValue(val,ObjectMetadata.TREE_MODEL_PARENT_KEY);
		if(p == null){
			p = MapHelper.readValue(val,"parent");
			val.remove("parent");
		}
		if(p != null){
			if(p instanceof Map){
				parent = (Map<String,Object>)p;
			}else {
				parent = dao.getByID(p.toString(), parentCollection);
			}
		}
		val.put(ObjectMetadata.TREE_MODEL_PARENT_KEY,parent);
		
		List<Object> paths = null;
		if(parent != null){
			paths = (List<Object>)MapHelper.readValue(parent,ObjectMetadata.TREE_MODEL_PATH_KEY);
			if(paths == null){
				paths = new ArrayList<Object>();
			}
			paths.add(parent);
		}
		val.put(ObjectMetadata.TREE_MODEL_PATH_KEY,paths);
	} **/
	
	public boolean isTreeModel(ObjectMetadata meta){
		if(meta == null || meta.getModelType() == null){
			return false;
		}
		
		return ModelType.TREE.is(meta.getModelType());
	}
	
	public void removeTreeFields(ObjectMetadata meta){
		if(meta == null || Utils.isEmpty(meta.getFields())){
			return;
		}
		
		List<ObjectField> removes = new ArrayList<ObjectField>();
		for(ObjectField f:meta.getFields()){
			if(ObjectMetadata.TREE_MODEL_PARENT_KEY.equals(f.getName()) 
					|| ObjectMetadata.TREE_MODEL_PATH_KEY.equals(f.getName()) ){
				removes.add(f);
			}
		}
		
		meta.getFields().removeAll(removes);
	}
	
	public List<ObjectField> treeFields(String linkedName){
		List<ObjectField> fields = new ArrayList<ObjectField>();
		
		ObjectField parent = new ObjectField();
		parent.setName(ObjectMetadata.TREE_MODEL_PARENT_KEY);
		parent.setType(FieldType.LINK_COLLECTION.getCode());
		parent.setLinkedObject(linkedName);
		fields.add(parent);
		
		ObjectField path = new ObjectField();
		path.setName(ObjectMetadata.TREE_MODEL_PATH_KEY);
		path.setType(FieldType.ARRAY.getCode());
		path.setElementType(FieldType.LINK_COLLECTION.getCode());
		path.setLinkedObject(parent.getLinkedObject());
		fields.add(path);
		
		return fields;
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
		Object oldParent = MapHelper.readValue(oldVal,ObjectMetadata.TREE_MODEL_PARENT_KEY+".id");
		Object newParent = MapHelper.readValue(newVal,ObjectMetadata.TREE_MODEL_PARENT_KEY);
		
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
	public void updateChildrenPaths(Map<String,Object> oldVal,
			Map<String,Object> newVal,ObjectMetadata meta,CollectionDao dao){
		if(!isTreeModel(meta) || sameParent(oldVal,newVal)){
			return;
		}
		
		/***
		 * ��ȡ·����
		 * ��·����oldVal._path_
		 * ��·����newVal._path_
		 * **/
		String oid = dao.readObjectIdToString(oldVal);
		List<Object> oldPath = Utils.toList(MapHelper.readValue(oldVal,ObjectMetadata.TREE_MODEL_PATH_KEY+".id"),true);
		List<Object> newPath = Utils.toList(MapHelper.readValue(newVal,ObjectMetadata.TREE_MODEL_PATH_KEY),true);
		
		oldPath.add(oid);
		newPath.add(oid);
		
		System.out.println("moveChildTree oldPath : " + oldPath);
		System.out.println("moveChildTree newPath : " + newPath);
		
		/**
		 * ���ݾ�·�����Ҫ�����������ӽڵ��ID
		 * **/
		//String query = "{_path_:{$all:oldPath}}";
		BasicDBObject query = new BasicDBObject(dao.newMap(ObjectMetadata.TREE_MODEL_PATH_KEY,dao.newMap("$all",oldPath)));
		
		String collName = meta.getName();
		List<Map<String,Object>> list = dao.find(query, collName);
		if(Utils.isEmpty(list)){
			return;
		}
		
		List<ObjectId> ids = new ArrayList<ObjectId>();
		for(Map<String,Object> data : list){
			ids.add(new ObjectId((String)data.get("id")));
		}
		query = dao.build(dao.newMap("_id",dao.newMap("$in",ids)));
		
		
		/***
		 * ɾ���ӽڵ�ľ�·��
		 * **/
		//String pull = "{$pull:{_path_:{$in:oldPath}}}";
		BasicDBObject pull = new BasicDBObject(dao.newMap("$pull", dao.newMap(ObjectMetadata.TREE_MODEL_PATH_KEY,dao.newMap("$in",oldPath))));
		
		/***
		 * ������·��
		 * **/
		//String push = "{$push:{_path_:{$each:newPath,$position:0}}}";
		Map<String,Object> pushPath = dao.newMap("$each",newPath);
		pushPath.put("$position",0);
		BasicDBObject push = new BasicDBObject(dao.newMap("$push",dao.newMap(ObjectMetadata.TREE_MODEL_PATH_KEY,pushPath)));

		DBCollection db = dao.getDBCollection(collName);
		db.updateMulti(query, pull);
		db.updateMulti(query, push);
		
		System.out.println("moveChildTree query : " + query);
		System.out.println("moveChildTree pull : " + pull);
		System.out.println("moveChildTree push : " + push);
		System.out.println("moveChildTree id query list : " + dao.find(query, collName));
	}
}
