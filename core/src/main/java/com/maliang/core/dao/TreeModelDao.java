package com.maliang.core.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import com.maliang.core.arithmetic.AE;
import com.maliang.core.model.FieldType;
import com.maliang.core.model.ModelType;
import com.maliang.core.model.ObjectField;
import com.maliang.core.model.ObjectMetadata;
import com.maliang.core.service.MapHelper;
import com.maliang.core.util.Utils;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

public class TreeModelDao {
	
	public static void main(String[] args) {
//		resetAllPath("");
	}
	
	public void resetPath(String collName) {
		BasicDao dao = new BasicDao();
		
		/**
		 * 处理根目录[parent=null]的path
		 * **/
		Map query = dao.newMap(ObjectMetadata.TREE_MODEL_PARENT_KEY,null);
		Map set = dao.newMap(ObjectMetadata.TREE_MODEL_PATH_KEY,new ArrayList());
		dao.update(query,set,collName);
		
		List<Map<String,Object>> rts = dao.findByMap(query, collName);
		for(Map<String,Object> or : rts) {
			resetPath(or,collName,dao);
		}
	}
	
	public void resetPath(String oid,String collName,boolean resetChildren) {
		BasicDao dao = new BasicDao();
		
		Map<String,Object> node = dao.getByID(oid, collName);
		if(Utils.isEmpty(node)) {
			return;
		}
		
		Object parent = MapHelper.readValue(node,"_parent");
		Object path = new ArrayList();
		if(!Utils.isEmpty(parent)) {
			path = MapHelper.readValue(parent,"_path.id");
			path = Utils.toList(path,true);
			((List)path).add(MapHelper.readValue(parent,"id").toString());
		}
		
		Map oidQuery = dao.newMap("_id",new ObjectId(oid));
		Map pathSet = dao.newMap("_path",path);
		dao.update(oidQuery,pathSet, collName);
		
		/**
		 * 是否处理子对象的path
		 * **/
		if(resetChildren) {
			/**
			 * 根据ID获得未转换过的源数据
			 * ***/
			List<Map<String,Object>> rts = dao.findByMap(oidQuery, collName);
			if(!Utils.isEmpty(rts)) {
				resetPath(rts.get(0),collName,dao);
			}
		}
	}
	public void resetPath(Map<String,Object> node,String collName,BasicDao dao) {
		Object id = MapHelper.readValue(node,"_id");
		Object path = MapHelper.readValue(node,ObjectMetadata.TREE_MODEL_PATH_KEY);
		path = Utils.toList(path,true);
		((List)path).add(id.toString());
		
		Map query = dao.newMap(ObjectMetadata.TREE_MODEL_PARENT_KEY,id.toString());
		Map set = dao.newMap(ObjectMetadata.TREE_MODEL_PATH_KEY,path);
		
		dao.update(query,set,collName);
		
		List<Map<String,Object>> rts = dao.findByMap(query , collName);
		if(!Utils.isEmpty(rts)) {
			for(Map<String,Object> rt : rts) {
				resetPath(rt,collName,dao);
			}
		}
	}
	
	
	/***
	 * ����Treeģ�͵�ר���ֶΣ�_parent_, _path_
	 *
	protected void doTreeModel(Map val,ObjectMetadata metadata,BasicDao dao){
		if(metadata == null || !ModelType.TREE.is(metadata.getModelType())){
			return;
		}源
		
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
			System.out.println("------------- f : " + f);
			
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
	
	public Object readParent(Map<String,Object> val){
		Object parent = MapHelper.readValue(val,ObjectMetadata.TREE_MODEL_PARENT_KEY);
		if(parent == null) {
			parent = MapHelper.readValue(val,ObjectMetadata.TREE_MODEL_PARENT_ALIAS);
		}
		return parent;
	}
	
	public String readStringID(Object val){
		if(val == null) {
			return null;
		}
		
		Object id = val;
		if(val instanceof Map) {
			id = MapHelper.readValue(val,"id");
		}
		
		if(id != null) {
			return id.toString();
		}
		return null;
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
		Object oldPid = this.readStringID(this.readParent(oldVal));
		Object newPid = this.readStringID(this.readParent(newVal));
		
//		Object oldParent = MapHelper.readValue(oldVal,ObjectMetadata.TREE_MODEL_PARENT_KEY+".id");
//		Object newParent = MapHelper.readValue(newVal,ObjectMetadata.TREE_MODEL_PARENT_KEY);
		
		if(Utils.isEmpty(oldPid) && Utils.isEmpty(newPid)){
			return true;
		}
		
		if(Utils.isEmpty(oldPid)){
			return false;
		}
		
		if(Utils.isEmpty(newPid)){
			return false;
		}
		
		return oldPid.equals(newPid);
	}
	
	public void deleteIncludeChildren(String oid,String collName,CollectionDao dao){
		
		Map<String, Object> nodeDoc = dao.getByID(oid, collName);

		List<Object> path = Utils.toList(MapHelper.readValue(nodeDoc,ObjectMetadata.TREE_MODEL_PATH_KEY+".id"),true);
		BasicDBObject pathQuery = new BasicDBObject(dao.newMap(ObjectMetadata.TREE_MODEL_PATH_KEY,dao.newMap("$all",path)));

		DBCollection db = dao.getDBCollection(collName);
		db.remove(pathQuery);
	}
	
	/***
	 * 删除节点：
	 * 1. 将“被删除节点”的直接子节点移动到其父节点下（没有父节点直接移动到根目录）
	 * 2. 修改所有子节点的路径
	 * 3. 执行“删除该节点的数据库记录”
	 * **/
	public void deleteTreeField(String oid,String collName,CollectionDao dao){
		Map<String, Object> nodeDoc = dao.getByID(oid, collName);
		Object parent = this.readParent(nodeDoc);
		String pid = this.readStringID(parent);
		
		/***
		 * 移动“被删除节点”的直接子节点
		 * **/
		//query : {parent:oid}
		BasicDBObject query = new BasicDBObject(ObjectMetadata.TREE_MODEL_PARENT_KEY,oid);
		
		//set: {$set:{parent:pid}}
		BasicDBObject set = new BasicDBObject("$set",dao.newMap(ObjectMetadata.TREE_MODEL_PARENT_KEY,pid));
		
		DBCollection db = dao.getDBCollection(collName);
		db.updateMulti(query, set);
		
		List<Object> oldPath = Utils.toList(MapHelper.readValue(nodeDoc,ObjectMetadata.TREE_MODEL_PATH_KEY+".id"),true);
		List<Object> newPath = Utils.toList(MapHelper.readValue(parent,ObjectMetadata.TREE_MODEL_PATH_KEY+".id"),true);
		
		doUpdatePath(oldPath,newPath,collName,dao);
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
		String oid = this.readStringID(oldVal);
		List<Object> oldPath = Utils.toList(MapHelper.readValue(oldVal,ObjectMetadata.TREE_MODEL_PATH_KEY+".id"),true);
		List<Object> newPath = Utils.toList(MapHelper.readValue(newVal,ObjectMetadata.TREE_MODEL_PATH_KEY),true);
		
		oldPath.add(oid);
		newPath.add(oid);
		
		System.out.println("moveChildTree oldPath : " + oldPath);
		System.out.println("moveChildTree newPath : " + newPath);
		
		doUpdatePath(oldPath,newPath,meta.getName(),dao);
//		
//		/**
//		 * ���ݾ�·�����Ҫ�����������ӽڵ��ID
//		 * **/
//		//String query = "{_path_:{$all:oldPath}}";
//		BasicDBObject query = new BasicDBObject(dao.newMap(ObjectMetadata.TREE_MODEL_PATH_KEY,dao.newMap("$all",oldPath)));
//		
//		String collName = meta.getName();
//		List<Map<String,Object>> list = dao.find(query, collName);
//		if(Utils.isEmpty(list)){
//			return;
//		}
//		
//		List<ObjectId> ids = new ArrayList<ObjectId>();
//		for(Map<String,Object> data : list){
//			ids.add(new ObjectId((String)data.get("id")));
//		}
//		query = dao.build(dao.newMap("_id",dao.newMap("$in",ids)));
//		
//		
//		/***
//		 * ɾ���ӽڵ�ľ�·��
//		 * **/
//		//String pull = "{$pull:{_path_:{$in:oldPath}}}";
//		BasicDBObject pull = new BasicDBObject(dao.newMap("$pull", dao.newMap(ObjectMetadata.TREE_MODEL_PATH_KEY,dao.newMap("$in",oldPath))));
//		
//		/***
//		 * ������·��
//		 * **/
//		//String push = "{$push:{_path_:{$each:newPath,$position:0}}}";
//		Map<String,Object> pushPath = dao.newMap("$each",newPath);
//		pushPath.put("$position",0);
//		BasicDBObject push = new BasicDBObject(dao.newMap("$push",dao.newMap(ObjectMetadata.TREE_MODEL_PATH_KEY,pushPath)));
//
//		DBCollection db = dao.getDBCollection(collName);
//		db.updateMulti(query, pull);
//		db.updateMulti(query, push);
//		
//		System.out.println("moveChildTree query : " + query);
//		System.out.println("moveChildTree pull : " + pull);
//		System.out.println("moveChildTree push : " + push);
//		System.out.println("moveChildTree id query list : " + dao.find(query, collName));
	}
	
	private void doUpdatePath(List<Object> oldPath,
			List<Object> newPath,String collName,CollectionDao dao){
		/**
		 * ���ݾ�·�����Ҫ�����������ӽڵ��ID
		 * **/
		//String query = "{_path_:{$all:oldPath}}";
		BasicDBObject query = new BasicDBObject(dao.newMap(ObjectMetadata.TREE_MODEL_PATH_KEY,dao.newMap("$all",oldPath)));
		
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
